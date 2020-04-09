package bogomolov.aa.wordstrainer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.android.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.android.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.android.setSetting
import bogomolov.aa.wordstrainer.repository.GoogleSheetsRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.SheetsScopes
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    internal lateinit var googleSheetsRepository: GoogleSheetsRepository

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val useGoogleSheet = getSetting<Boolean>(this, USE_GOOGLE_SHEET)!!
        if (useGoogleSheet && !googleSheetsRepository.hasCredential()) {
            requestSignIn(this)
        }
        initTranslateDirection()
    }

    private fun initTranslateDirection() {
        if (getSetting<String>(this, TRANSLATION_DIRECTION) == null) {
            val defaultLanguage = "en"
            val locale = Locale.getDefault()
            val direction =
                if (locale.language == defaultLanguage) {
                    "de-$defaultLanguage"
                } else {
                    "$defaultLanguage-${locale.language}"
                }
            setSetting(this, TRANSLATION_DIRECTION, direction)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val set = data?.extras
        Log.i("test", "onActivityResult resultCode $resultCode googleSignInStatus: ${set?.get("googleSignInStatus")}")
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Log.i("test", "getSignedInAccountFromIntent")
                GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener { account ->
                        Log.i("test", "addOnSuccessListener")
                        val scopes = listOf(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE)
                        val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
                        credential.selectedAccount = account.account
                        setSetting(this, USE_GOOGLE_SHEET, true)
                        googleSheetsRepository.setCredential(credential)
                    }.addOnFailureListener { e ->
                        Log.i("test", "addOnFailureListener ${e.message}")
                        e.printStackTrace()
                    }
            }
        }
    }

    fun requestSignIn(context: Context) {
        Log.i("test", "requestSignIn")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestScopes(Scope(SheetsScopes.DRIVE))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, signInOptions)
        startActivityForResult(client.signInIntent, REQUEST_SIGN_IN)
    }

    companion object {
        private const val REQUEST_SIGN_IN = 1
    }
}
