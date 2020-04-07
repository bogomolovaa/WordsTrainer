package bogomolov.aa.wordstrainer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bogomolov.aa.wordstrainer.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject


class MainActivity : AppCompatActivity() , HasAndroidInjector {
    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //requestSignIn(this)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("test", "onActivityResult resultCode $resultCode data $data")
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.i("test", "getSignedInAccountFromIntent")
                GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener { account ->
                        Log.i("test", "addOnSuccessListener")
                        val scopes = listOf(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE)
                        val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
                        credential.selectedAccount = account.account

                        val jsonFactory = JacksonFactory.getDefaultInstance()
                        // GoogleNetHttpTransport.newTrustedTransport()
                        //val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
                        val httpTransport = AndroidHttp.newCompatibleTransport()
                        //createSpreadsheet(service)
                        GlobalScope.launch(Dispatchers.IO) {
                            //loadSheet(service)
                            //addRow(getSheetsService(httpTransport, jsonFactory, credential))
                            getAllSheets(getDriveService(httpTransport, jsonFactory, credential))
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.i("test", "addOnFailureListener ${e.message}")
                        e.printStackTrace()
                    }
            }
        }
        if (requestCode == SELECT_SHEET_ID) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data!!
                val mimeType = contentResolver.getType(uri)
                Log.i("test", "mimeType: $mimeType")
                contentResolver.openInputStream(uri).use { inS ->
                    BufferedReader(InputStreamReader(inS!!)).use { reader ->
                        val stringBuilder = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                        Log.i("test", "content: ${stringBuilder.toString()}")
                    }
                }
            }
        }
    }

    private fun getAllSheets(drive: Drive) {
        val filesList = drive.files().list()
            .setQ("mimeType ='$TYPE_GOOGLE_SHEETS'")
            .setSpaces("drive")
            .setFields("files(id, name,size)")
            .execute()
        if (filesList != null) {
            for (file in filesList.files) {
                Log.i("", "file: ${file.name} ${file.id}")
            }
        } else {
            Log.i("test", "null filesList")
        }
    }

    private fun getSheetsService(
        httpTransport: HttpTransport,
        jsonFactory: JacksonFactory,
        credential: GoogleAccountCredential
    ) =
        Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(getString(R.string.app_name))
            .build()

    private fun getDriveService(
        httpTransport: HttpTransport,
        jsonFactory: JacksonFactory,
        credential: GoogleAccountCredential
    ) =
        Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(getString(R.string.app_name))
            .build();

    private fun requestSignIn(context: Context) {
        /*
        GoogleSignIn.getLastSignedInAccount(context)?.also { account ->
            Timber.d("account=${account.displayName}")
        }
         */
        Log.i("test", "requestSignIn")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestScopes(Scope(SheetsScopes.DRIVE))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, signInOptions)

        startActivityForResult(client.signInIntent,
            REQUEST_SIGN_IN
        )
    }

    private fun loadSheet(service: Sheets) {
        val spreadsheetId = "1dUPN6sSH10Cp0Uxmnw7Gwo0ccJoj8icnBCsKOL0tDaM"
        val range = "A1:C"

        val response = service.spreadsheets().values().get(spreadsheetId, range).execute()
        val values =
            response.getValues()
        if (values == null || values.isEmpty()) {
            Log.i("test", "No data found.")
        } else {
            Log.i("test", "Values")
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                Log.i("test", "$row")
            }
        }
    }

    private fun createSpreadsheet(service: Sheets) {
        Log.i("test", "createSpreadsheet")
        var spreadsheet = Spreadsheet()
            .setProperties(
                SpreadsheetProperties()
                    .setTitle("CreateNewSpreadsheet")
            )

        spreadsheet = service.spreadsheets().create(spreadsheet).execute()
        Log.i("test", "ID: ${spreadsheet.spreadsheetId}")
    }

    private fun addRow(service: Sheets): Boolean {
        val spreadsheetId = "1dUPN6sSH10Cp0Uxmnw7Gwo0ccJoj8icnBCsKOL0tDaM"
        val range = "A1:C"
        val row1: MutableList<Any> = ArrayList()
        row1.add("Name")
        row1.add("Rollno")
        row1.add("Class")
        val values = ArrayList<List<Any>>()
        values.add(row1)
        val valueRange = ValueRange()
        valueRange.setMajorDimension("ROWS")
        valueRange.setValues(values)
        val response = service.spreadsheets().values()
            .append(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
            .execute()
        Log.i("Update_response", response.toString())
        return true
    }

    companion object {
        private const val REQUEST_SIGN_IN = 1
        private const val SELECT_SHEET_ID = 2
        private const val TYPE_GOOGLE_SHEETS = "application/vnd.google-apps.spreadsheet"
    }
}
