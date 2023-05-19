package bogomolov.aa.wordstrainer.features.google_sheets.di

import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsFragment
import dagger.Subcomponent

@Subcomponent
interface GoogleSheetsComponent {

    fun inject(fragment: GoogleSheetsFragment)

    @Subcomponent.Builder
    interface Builder {
        fun build(): GoogleSheetsComponent
    }

}