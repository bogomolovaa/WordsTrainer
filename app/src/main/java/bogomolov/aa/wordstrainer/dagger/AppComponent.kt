package bogomolov.aa.wordstrainer.dagger

import android.app.Application
import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsFragment
import bogomolov.aa.wordstrainer.features.main.MainActivity
import bogomolov.aa.wordstrainer.features.repetition.RepetitionFragment
import bogomolov.aa.wordstrainer.features.settings.SettingsFragment
import bogomolov.aa.wordstrainer.features.translation.TranslationFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelsModule::class, MainModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: GoogleSheetsFragment)

    fun inject(fragment: RepetitionFragment)

    fun inject(fragment: TranslationFragment)

    fun inject(fragment: SettingsFragment)


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}