package bogomolov.aa.wordstrainer.dagger

import bogomolov.aa.wordstrainer.view.MainActivity
import bogomolov.aa.wordstrainer.view.fragments.GoogleSheetsFragment
import bogomolov.aa.wordstrainer.view.fragments.RepetitionFragment
import bogomolov.aa.wordstrainer.view.fragments.SettingsFragment
import bogomolov.aa.wordstrainer.view.fragments.TranslationFragment
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

}