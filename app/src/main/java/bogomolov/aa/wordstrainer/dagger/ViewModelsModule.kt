package bogomolov.aa.wordstrainer.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsViewModel
import bogomolov.aa.wordstrainer.features.repetition.RepetitionViewModel
import bogomolov.aa.wordstrainer.features.settings.SettingsViewModel
import bogomolov.aa.wordstrainer.features.translation.TranslationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(GoogleSheetsViewModel::class)
    abstract fun bindGoogleSheetsViewModel(googleSheetsViewModel: GoogleSheetsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RepetitionViewModel::class)
    abstract fun bindRepetitionViewModel(repetitionViewModel: RepetitionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TranslationViewModel::class)
    abstract fun bindTranslateViewModel(translationViewModel: TranslationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}