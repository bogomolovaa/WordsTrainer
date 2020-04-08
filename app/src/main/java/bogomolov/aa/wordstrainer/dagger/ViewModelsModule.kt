package bogomolov.aa.wordstrainer.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bogomolov.aa.wordstrainer.viewmodel.GoogleSheetsViewModel
import bogomolov.aa.wordstrainer.viewmodel.RepetitionViewModel
import bogomolov.aa.wordstrainer.viewmodel.TranslationViewModel
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
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}