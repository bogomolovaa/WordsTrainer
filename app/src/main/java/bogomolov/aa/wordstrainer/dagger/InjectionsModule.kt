package bogomolov.aa.wordstrainer.dagger

import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsFragment
import bogomolov.aa.wordstrainer.features.main.MainActivity
import bogomolov.aa.wordstrainer.features.repetition.RepetitionFragment
import bogomolov.aa.wordstrainer.features.settings.SettingsFragment
import bogomolov.aa.wordstrainer.features.translation.TranslationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class InjectionsModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindGoogleSheetsFragment(): GoogleSheetsFragment

    @ContributesAndroidInjector
    abstract fun bindRepetitionFragment(): RepetitionFragment

    @ContributesAndroidInjector
    abstract fun bindTranslationFragment(): TranslationFragment

    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment
}