package bogomolov.aa.wordstrainer.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.repository.GoogleSheetsRepository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel
@Inject constructor(
    private val roomRepository: RoomRepository,
    private val googleSheetsRepository: GoogleSheetsRepository
) : ViewModel() {

    fun initWords() {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.initWords()
        }
    }

    fun importWords() {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.import(googleSheetsRepository.words)
        }
    }

    fun exportWords() {
        viewModelScope.launch(Dispatchers.IO) {
            googleSheetsRepository.export(roomRepository.words)
        }
    }
}