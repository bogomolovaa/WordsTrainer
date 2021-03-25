package bogomolov.aa.wordstrainer.repository

import android.content.Context
import android.widget.Toast
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.android.GOOGLE_SHEET_ID
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.domain.GoogleSheet
import bogomolov.aa.wordstrainer.repository.entity.Word
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

private const val TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet"
private const val ROW_RANGE = "A1:D"

@Singleton
class GoogleSheetsRepository @Inject constructor(
    val context: Context,
    translateProvider: YandexTranslateProvider
) : Repository(context, translateProvider) {
    private var credential: GoogleAccountCredential? = null
    private var sheetsService: Sheets? = null
    private var driveService: Drive? = null

    fun setCredential(credential: GoogleAccountCredential) {
        this.credential = credential
        GlobalScope.launch(Dispatchers.IO) {
            initWords()
        }
    }

    fun hasCredential() = credential != null

    override fun update(word: Word) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null)
            getSheetsService()?.let { updateWords(it, googleSheetId, listOf(word)) }
    }

    override fun addWord(word: Word) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null)
            getSheetsService()?.let { addWordsToSheet(it, googleSheetId, listOf(word)) }
    }

    override fun loadAllWords(): List<Word> {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null)
            return getSheetsService()?.let { loadSheet(it, googleSheetId) } ?: listOf()
        return listOf()
    }

    fun export(words: List<Word>) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null)
            getSheetsService()?.let { exportToSheet(it, googleSheetId, words) }
    }

    fun getAllSheets(): List<GoogleSheet> {
        val googleSheets = ArrayList<GoogleSheet>()
        val service = getDriveService()
        if (service != null) kotlin.runCatching {
            service.files().list().setQ("mimeType ='${TYPE_GOOGLE_SHEETS}'").setSpaces("drive")
                .setFields("files(id, name,size)").execute()
        }.onSuccess { filesList ->
            if (filesList != null)
                for (file in filesList.files) googleSheets += GoogleSheet(file.name, file.id)
        }.onFailure(showError)
        return googleSheets
    }

    fun createSpreadsheet(name: String): String? {
        val service = getSheetsService()
        if (service != null) kotlin.runCatching {
            val sheet = Spreadsheet().setProperties(SpreadsheetProperties().setTitle(name))
            return service.spreadsheets().create(sheet).execute().spreadsheetId
        }.onFailure(showError)
        return null
    }


    private fun loadSheet(service: Sheets, spreadsheetId: String): List<Word> {
        val words = ArrayList<Word>()
        kotlin.runCatching {
            service.spreadsheets().values().get(spreadsheetId, ROW_RANGE).execute().getValues()
        }.onSuccess { values ->
            if (values != null) for (row in values) if (row.size == 4) words += rowToWord(row)
        }.onFailure(showError)
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
        kotlin.runCatching {
            service.spreadsheets().values().append(sheetId, ROW_RANGE, getValueRange(newWords))
                .setValueInputOption("RAW").execute()
        }.onFailure(showError)
    }

    private fun updateWords(service: Sheets, spreadsheetId: String, words: List<Word>) {
        val batchRequest = BatchUpdateValuesRequest()
        batchRequest.valueInputOption = "RAW"
        batchRequest.data = words.map { getRankValueRange(it) }
        kotlin.runCatching {
            service.spreadsheets().values().batchUpdate(spreadsheetId, batchRequest).execute()
        }.onFailure(showError)
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
        kotlin.runCatching {
            if (existedWords.size > 0) updateWords(service, spreadsheetId, existedWords)
            if (newWords.size > 0) addWordsToSheet(service, spreadsheetId, newWords)
            for (word in newWords) wordsMap[word.word] = word
            this.words.addAll(newWords)
            updateWordsRanger()
        }.onFailure(showError)
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
        ).setApplicationName(context.getString(R.string.app_name))
            .build() else null
    }

    private fun createDriveService(): Drive? {
        return if (credential != null) Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(context.getString(R.string.app_name))
            .build() else null
    }

    private fun getGoogleSheetId() = getSetting<String>(context, GOOGLE_SHEET_ID)

    private val showError = { e: Throwable ->
        if (e is UnknownHostException || e is SocketTimeoutException) {
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.no_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

private fun getRankRange(word: Word) = "C${word.id}"