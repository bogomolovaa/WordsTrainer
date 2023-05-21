package bogomolov.aa.wordstrainer.features.google_sheets

import android.content.Context
import android.content.Intent
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.features.shared.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.features.shared.getSetting
import bogomolov.aa.wordstrainer.features.shared.setSetting
import bogomolov.aa.wordstrainer.repository.Repository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

private const val TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet"
private const val ROW_RANGE = "A1:D"
const val GOOGLE_SHEET_ID = "google_sheet_id"

class GoogleSheetsRepository constructor(
    val context: Context,
    val appName: String
) : Repository(context) {
    private var credential: GoogleAccountCredential? = null
    private var sheetsService: Sheets? = null
    private var driveService: Drive? = null

    fun getSignInIntent(): Intent {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestScopes(Scope(SheetsScopes.DRIVE))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, signInOptions).signInIntent
    }

    fun signIn(data: Intent?, onResult: (Boolean) -> Unit) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener { account ->
                val scopes = listOf(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE)
                val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
                credential.selectedAccount = account.account
                setSetting(context, USE_GOOGLE_SHEET, true)
                this.credential = credential
                initWords()
                onResult(true)
            }.addOnFailureListener { e ->
                onResult(false)
            }
    }

    fun hasCredential() = credential != null

    override fun update(word: Word) = Completable.create { emitter ->
        kotlin.runCatching {
            val googleSheetId = getGoogleSheetId()
            if (googleSheetId != null && credential != null)
                getSheetsService()?.let { updateWords(it, googleSheetId, listOf(word)) }
            emitter.onComplete()
        }.onFailure { emitter.onError(it) }
    }.subscribeOn(Schedulers.io())

    override fun addWord(word: Word, direction: String?) = Completable.create { emitter ->
        kotlin.runCatching {
            val googleSheetId = getGoogleSheetId()
            if (googleSheetId != null && credential != null)
                getSheetsService()?.let { addWordsToSheet(it, googleSheetId, listOf(word)) }
            emitter.onComplete()
        }.onFailure { emitter.onError(it) }
    }.subscribeOn(Schedulers.io())

    override fun loadAllWords() = Single.create<List<Word>> { emitter ->
        kotlin.runCatching {
            val googleSheetId = getGoogleSheetId()
            emitter.onSuccess(
                if (googleSheetId != null && credential != null)
                    getSheetsService()?.let { loadSheet(it, googleSheetId) } ?: listOf()
                else listOf()
            )
        }.onFailure { emitter.onError(it) }
    }.subscribeOn(Schedulers.io())

    fun export(words: List<Word>) = Completable.create { emitter ->
        kotlin.runCatching {
            val googleSheetId = getGoogleSheetId()
            if (googleSheetId != null && credential != null)
                getSheetsService()?.let { exportToSheet(it, googleSheetId, words) }
            emitter.onComplete()
        }.onFailure { emitter.onError(it) }
    }.subscribeOn(Schedulers.io())

    fun getAllSheets(): Single<List<GoogleSheet>> = Single.create<List<GoogleSheet>> { emitter ->
        val googleSheets = ArrayList<GoogleSheet>()
        val service = getDriveService()
        if (service != null) kotlin.runCatching {
            service.files().list().setQ("mimeType ='$TYPE_GOOGLE_SHEETS'").setSpaces("drive")
                .setFields("files(id, name,size)").execute()
        }.onSuccess { filesList ->
            if (filesList != null)
                for (file in filesList.files) googleSheets += GoogleSheet(file.name, file.id)
        }.onFailure { emitter.onError(it) }
        emitter.onSuccess(googleSheets)
    }.subscribeOn(Schedulers.io())

    fun createSpreadsheet(name: String): Single<String> = Single.create<String> { emitter ->
        val service = getSheetsService()
        if (service != null) kotlin.runCatching {
            val sheet = Spreadsheet().setProperties(SpreadsheetProperties().setTitle(name))
            emitter.onSuccess(service.spreadsheets().create(sheet).execute().spreadsheetId)
        }.onFailure { emitter.onError(it) }
    }.subscribeOn(Schedulers.io())

    private fun loadSheet(service: Sheets, spreadsheetId: String): List<Word> {
        val words = ArrayList<Word>()
        service.spreadsheets().values().get(spreadsheetId, ROW_RANGE).execute().getValues()?.let {
            for (row in it) if (row.size == 4) words += rowToWord(row)
        }
        return words
    }

    private fun rowToWord(row: List<Any>): Word {
        val rankString = row[2] as String
        return Word(
            word = row[0] as String,
            id = row[1].toString().toInt(),
            rank = if (rankString.isNotEmpty()) rankString.toInt() else 0,
            translation = row[3] as String
        )
    }

    private fun getValueRange(words: List<Word>) =
        ValueRange().apply {
            majorDimension = "ROWS"
            setValues(words.map { word ->
                listOf(word.word, word.id, word.rank, word.translation)
            })
        }

    private fun getRankValueRange(word: Word) =
        ValueRange().apply {
            majorDimension = "ROWS"
            setValues(listOf(listOf(word.rank)))
            this.range = getRankRange(word)
        }

    private fun addWordsToSheet(service: Sheets, sheetId: String, newWords: List<Word>) {
        for ((id, newWord) in newWords.withIndex()) newWord.id = words.size + 1 + id
        service.spreadsheets().values().append(sheetId, ROW_RANGE, getValueRange(newWords))
            .setValueInputOption("RAW").execute()
    }

    private fun updateWords(service: Sheets, spreadsheetId: String, words: List<Word>) {
        val batchRequest = BatchUpdateValuesRequest()
        batchRequest.valueInputOption = "RAW"
        batchRequest.data = words.map { getRankValueRange(it) }
        service.spreadsheets().values().batchUpdate(spreadsheetId, batchRequest).execute()
    }

    private fun exportToSheet(service: Sheets, spreadsheetId: String, words: List<Word>) {
        val existedWords = ArrayList<Word>()
        val newWords = ArrayList<Word>()
        for (word in words) {
            val existed = wordsMap[word.word]
            if (existed != null) {
                if (existed.rank != word.rank) {
                    existed.rank = word.rank
                    existedWords += existed
                }
            } else {
                newWords += word.copy(id = 0)
            }
        }
        if (existedWords.size > 0) updateWords(service, spreadsheetId, existedWords)
        if (newWords.size > 0) addWordsToSheet(service, spreadsheetId, newWords)
        for (word in newWords) wordsMap[word.word] = word
        this.words.addAll(newWords)
        updateWordsRanger()
    }

    private fun getSheetsService(): Sheets? {
        if (sheetsService == null) sheetsService = createSheetsService()
        return sheetsService
    }

    private fun getDriveService(): Drive? {
        if (driveService == null) driveService = createDriveService()
        return driveService
    }

    private fun createSheetsService(): Sheets? {
        return if (credential != null) Sheets.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(appName)
            .build() else null
    }

    private fun createDriveService(): Drive? {
        return if (credential != null) Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(appName)
            .build() else null
    }

    private fun getGoogleSheetId() = getSetting<String>(context, GOOGLE_SHEET_ID)
}

private fun getRankRange(word: Word) = "C${word.id}"