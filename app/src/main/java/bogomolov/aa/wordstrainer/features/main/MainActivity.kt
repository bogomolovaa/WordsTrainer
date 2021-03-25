package bogomolov.aa.wordstrainer.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.android.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.android.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.android.setSetting
import bogomolov.aa.wordstrainer.databinding.ActivityMainBinding
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

private const val REQUEST_SIGN_IN = 1

class MainActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    internal lateinit var googleSheetsRepository: GoogleSheetsRepository

    override fun androidInjector() = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val useGoogleSheet = getSetting<Boolean>(this, USE_GOOGLE_SHEET) ?: false
        if (useGoogleSheet && !googleSheetsRepository.hasCredential()) requestSignIn(this)
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
        if (requestCode == REQUEST_SIGN_IN && resultCode == RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener { account ->
                    val scopes = listOf(SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE)
                    val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
                    credential.selectedAccount = account.account
                    setSetting(this, USE_GOOGLE_SHEET, true)
                    googleSheetsRepository.setCredential(credential)
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        resources.getString(R.string.sign_in_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun requestSignIn(context: Context) {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestScopes(Scope(SheetsScopes.DRIVE))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, signInOptions)
        startActivityForResult(client.signInIntent, REQUEST_SIGN_IN)
    }
}