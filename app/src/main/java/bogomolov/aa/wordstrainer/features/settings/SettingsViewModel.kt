package bogomolov.aa.wordstrainer.features.settings

import androidx.lifecycle.ViewModel
import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsRepository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import javax.inject.Inject

class SettingsViewModel
@Inject constructor(
    private val roomRepository: RoomRepository,
    private val googleSheetsRepository: GoogleSheetsRepository
) : ViewModel() {


    fun initWords() {
        roomRepository.initWords()
    }

    fun importWords() {
        roomRepository.import(googleSheetsRepository.words).subscribe()
    }

    fun exportWords() {
        googleSheetsRepository.export(roomRepository.words).subscribe()
    }
}