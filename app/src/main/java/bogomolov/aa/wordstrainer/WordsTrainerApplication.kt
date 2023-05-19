package bogomolov.aa.wordstrainer

import android.app.Application
import bogomolov.aa.wordstrainer.dagger.AppComponent
import bogomolov.aa.wordstrainer.dagger.DaggerAppComponent

class WordsTrainerApplication: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().application(this).build()
    }
}