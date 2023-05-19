package bogomolov.aa.wordstrainer

import android.app.Application
import bogomolov.aa.wordstrainer.dagger.AppComponent
import bogomolov.aa.wordstrainer.dagger.DaggerAppComponent
import bogomolov.aa.wordstrainer.features.google_sheets.di.GoogleSheetsComponent
import bogomolov.aa.wordstrainer.features.google_sheets.di.GoogleSheetsComponentProvider

class WordsTrainerApplication: Application(), GoogleSheetsComponentProvider {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().application(this).build()
    }

    override fun get(): GoogleSheetsComponent {
        return appComponent.googleSheetsComponent().build()
    }
}