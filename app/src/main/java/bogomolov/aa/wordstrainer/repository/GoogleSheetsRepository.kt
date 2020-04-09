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
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetsRepository
@Inject constructor(
    val context: Context,
    translateProvider: YandexTranslateProvider
) : Repository(translateProvider) {

    init {
        Log.i("test","GoogleSheetsRepository created")
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

    override fun updateRank(word: Word, delta: Int) {
        word.rank += delta
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            updateRankCell(sheetsService!!, googleSheetId, word)
        }
    }

    override fun addWord(word: Word) {
        val googleSheetId = getGoogleSheetId()
        if (googleSheetId != null && credential != null) {
            if (sheetsService == null) sheetsService = getSheetsService()
            addWordToGoogleSheets(sheetsService!!, googleSheetId, word)
        }else{
            Log.i("test","addWord googleSheetId $googleSheetId credential $credential")
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
        val range = "A1:E"
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
                words += word
                Log.i("test", "$word")
            }
        }
        return words
    }

    private fun addWordToGoogleSheets(service: Sheets, spreadsheetId: String, word: Word) {
        word.id = words.size + 1
        val range = "A1:E"
        val row1: MutableList<Any> = ArrayList()
        row1.add(word.word)
        row1.add(word.translation)
        row1.add(word.id)
        row1.add(word.rank)
        row1.add(word.json)
        val values = ArrayList<List<Any>>()
        values.add(row1)
        val valueRange = ValueRange()
        valueRange.setMajorDimension("ROWS")
        valueRange.setValues(values)
        val response = service.spreadsheets().values()
            .append(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
            .execute()
        Log.i("test", "added $word response $response")
    }

    private fun updateRankCell(service: Sheets, spreadsheetId: String, word: Word) {
        val range = "D${word.id}"
        val row1: MutableList<Any> = ArrayList()
        row1.add(word.rank)
        val values = ArrayList<List<Any>>()
        values.add(row1)
        val valueRange = ValueRange()
        valueRange.setMajorDimension("ROWS")
        valueRange.setValues(values)
        val response = service.spreadsheets().values()
            .update(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
            .execute()
        Log.i("test", "updated $word")
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

    companion object {
        private const val TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet"
    }

}