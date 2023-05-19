package bogomolov.aa.wordstrainer.features.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.WordsTrainerApplication
import bogomolov.aa.wordstrainer.databinding.ActivityMainBinding
import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsRepository
import bogomolov.aa.wordstrainer.features.shared.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.features.shared.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.features.shared.getSetting
import bogomolov.aa.wordstrainer.features.shared.setSetting
import java.util.*
import javax.inject.Inject

private const val REQUEST_SIGN_IN = 1

class MainActivity : AppCompatActivity() {
    @Inject
    internal lateinit var googleSheetsRepository: GoogleSheetsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as WordsTrainerApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val useGoogleSheet = getSetting<Boolean>(
            this,
            USE_GOOGLE_SHEET
        ) ?: false
        if (useGoogleSheet && !googleSheetsRepository.hasCredential()) requestSignIn()
        initTranslateDirection()
    }

    private fun initTranslateDirection() {
        if (getSetting<String>(
                this,
                TRANSLATION_DIRECTION
            ) == null
        ) {
            val defaultLanguage = "en"
            val locale = Locale.getDefault()
            val direction =
                if (locale.language == defaultLanguage) {
                    "de-$defaultLanguage"
                } else {
                    "$defaultLanguage-${locale.language}"
                }
            setSetting(
                this,
                TRANSLATION_DIRECTION,
                direction
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN && resultCode == RESULT_OK) {
            googleSheetsRepository.signIn(data) {
                if (!it) Toast.makeText(
                    this,
                    resources.getString(R.string.sign_in_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun requestSignIn() {
        startActivityForResult(googleSheetsRepository.getSignInIntent(), REQUEST_SIGN_IN)
    }
}