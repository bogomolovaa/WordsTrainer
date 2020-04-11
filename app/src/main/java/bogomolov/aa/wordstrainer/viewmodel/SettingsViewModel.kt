package bogomolov.aa.wordstrainer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.android.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.android.setSetting
import bogomolov.aa.wordstrainer.repository.GoogleSheetsRepository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import bogomolov.aa.wordstrainer.repository.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel
@Inject constructor(
    private val context: Context,
    private val roomRepository: RoomRepository,
    private val googleSheetsRepository: GoogleSheetsRepository
) : ViewModel() {

    fun setDirection(direction: String) {
        setSetting(context, TRANSLATION_DIRECTION, direction)
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