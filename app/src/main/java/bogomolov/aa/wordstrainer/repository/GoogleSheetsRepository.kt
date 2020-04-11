package bogomolov.aa.wordstrainer.repository

import android.content.Context
import android.util.Log
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.android.GOOGLE_SHEET_ID
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.model.GoogleSheet
import bogomolov.aa.wordstrainer.repository.entity.Word
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GoogleSheetsRepository
@Inject constructor(
    val context: Context,
    translateProvider: YandexTranslateProvider
) : Repository(context, translateProvider) {

    init {
        Log.i("test", "GoogleSheetsRepository created")
    }

    private var credential: GoogleAccountCredential? = null
    private var sheetsService: Sheets? = null
    private var driveService: Drive? = null

    fun setCredential(credential: GoogleAccountCredential) {
        this.credential = credential
        Log.i("test", "set credential $credential")
        GlobalScope.launch(Dispatchers.IO) {
            initWords()
        }
    }

    fun hasCredential() = credential != null

    override fun update(word: Word) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            updateWords(sheetsService!!, googleSheetId, listOf(word))
        }
    }

    override fun delete(word: Word) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            updateWords(sheetsService!!, googleSheetId, listOf(word))
        }
    }

    override fun addWord(word: Word) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            addWordsToSheet(sheetsService!!, googleSheetId, listOf(word))
        } else {
            Log.i("test", "addWord googleSheetId $googleSheetId credential $credential")
        }
    }

    override fun loadAllWords(): List<Word> {
        Log.i("test", "GoogleSheetsRepository loadAllWords()")
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            return loadSheet(sheetsService!!, googleSheetId)
        }
        return listOf()
    }

    fun export(words: List<Word>) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            exportToSheet(sheetsService!!, googleSheetId, words)
        } else {
            Log.i("test", "export googleSheetId $googleSheetId credential $credential")
        }
    }

    private fun getGoogleSheetId() = getSetting<String>(context, GOOGLE_SHEET_ID)

    private fun getSheetsService(): Sheets? {
        return if (credential != null) Sheets.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(context.getString(R.string.app_name))
            .build() else null
    }

    private fun getDriveService(): Drive? {
        return if (credential != null) Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(context.getString(R.string.app_name))
            .build() else null
    }

    private fun loadSheet(service: Sheets, spreadsheetId: String): List<Word> {
        val range = getRowRange()
        val response = service.spreadsheets().values().get(spreadsheetId, range).execute()
        val values =
            response.getValues()
        val words = ArrayList<Word>()
        if (values == null || values.isEmpty()) {
            Log.i("test", "No data found.")
        } else {
            Log.i("test", "Values $values")
            for (row in values) {
                val word =
                    Word(
                        word = row[0] as String,
                        translation = row[1] as String,
                        id = row[2].toString().toInt(),
                        rank = row[3].toString().toInt(),
                        json = row[4] as String
                    )
                if (row.size == 6) word.deleted =
                    if (row[5].toString() == "") 0 else row[5].toString().toInt()
                words += word
                Log.i("test", "$word")
            }
        }
        return words
    }

    private fun getValueRange(
        words: List<Word>,
        range: String? = null,
        update: String? = null
    ): ValueRange {
        val values = ArrayList<List<Any>>()
        for (word in words) {
            val row: MutableList<Any> = ArrayList()
            if (update == null) {
                row.add(word.word)
                row.add(word.translation)
                row.add(word.id)
            }
            if (update == null || update == "rank") row.add(word.rank)
            if (update == null) row.add(word.json)
            if (update == null || update == "deleted") row.add(word.deleted)
            values.add(row)
        }
        val valueRange = ValueRange()
        valueRange.majorDimension = "ROWS"
        valueRange.setValues(values)
        if (range != null) valueRange.range = range
        return valueRange
    }

    private fun addWordsToSheet(
        service: Sheets,
        spreadsheetId: String,
        newWords: List<Word>
    ) {
        Log.i("test", "addWordsToGoogleSheets words.size ${words.size}")
        for ((id, newWord) in newWords.withIndex()) newWord.id = words.size + 1 + id
        val range = getRowRange()
        val valueRange = getValueRange(newWords)
        val response = service.spreadsheets().values()
            .append(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
            .execute()
        Log.i("test", "added $newWords response $response")
    }

    private fun getRankRange(word: Word) = "D${word.id}"
    private fun getDeletedRange(word: Word) = "F${word.id}"

    private fun getRowRange() = "A1:F"

    private fun updateWords(service: Sheets, spreadsheetId: String, words: List<Word>) {
        Log.i("test","updateWords $words")
        val batchRequest = BatchUpdateValuesRequest()
        batchRequest.valueInputOption = "RAW"
        batchRequest.data = words.map {
            getValueRange(listOf(it), getRankRange(it), "rank")
        } + words.map {
            getValueRange(listOf(it), getDeletedRange(it), "deleted")
        }
        val updateResponse: BatchUpdateValuesResponse =
            service.spreadsheets().values().batchUpdate(spreadsheetId, batchRequest).execute()
        Log.i("test", "export updateResponse $updateResponse")
    }

    fun getAllSheets(): List<GoogleSheet> {
        Log.i("test", "getAllSheets")
        if (driveService == null) driveService = getDriveService()
        val filesList = driveService!!.files().list()
            .setQ("mimeType ='${TYPE_GOOGLE_SHEETS}'")
            .setSpaces("drive")
            .setFields("files(id, name,size)")
            .execute()
        val googleSheets = ArrayList<GoogleSheet>()
        if (filesList != null) {
            for (file in filesList.files)
                googleSheets += GoogleSheet(file.name, file.id)
            Log.i("test", "getAllSheets size ${googleSheets.size}")
        } else {
            Log.i("test", "null filesList")
        }
        return googleSheets
    }


    fun createSpreadsheet(name: String): String? {
        try {
            if (sheetsService == null) sheetsService = getSheetsService()
            Log.i("test", "createSpreadsheet")
            var spreadsheet = Spreadsheet()
                .setProperties(
                    SpreadsheetProperties()
                        .setTitle(name)
                )
            spreadsheet = sheetsService!!.spreadsheets().create(spreadsheet).execute()
            Log.i("test", "ID: ${spreadsheet.spreadsheetId}")
            return spreadsheet.spreadsheetId
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun exportToSheet(service: Sheets, spreadsheetId: String, words: List<Word>) {
        val existedWords = ArrayList<Word>()
        val newWords = ArrayList<Word>()
        for (word in words) {
            val existed = wordsMap[word.word]
            if (existed != null) {
                if (existed.rank != word.rank || existed.deleted != word.deleted) {
                    existed.rank = word.rank
                    existed.deleted = word.deleted
                    existedWords += existed
                }
            } else {
                newWords += word.copy(id = 0)
            }
        }
        Log.i("test", "export: update: ${existedWords.size} add: ${newWords.size} ")
        if (existedWords.size > 0) updateWords(service, spreadsheetId, existedWords)
        if (newWords.size > 0) addWordsToSheet(service, spreadsheetId, newWords)
        for (word in newWords) wordsMap[word.word] = word
        this.words.addAll(newWords)
        updateWordsRanger()
    }


    companion object {
        private const val TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet"
    }

}