package bogomolov.aa.wordstrainer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.android.GOOGLE_SHEET_ID
import bogomolov.aa.wordstrainer.android.GOOGLE_SHEET_NAME
import bogomolov.aa.wordstrainer.android.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.android.setSetting
import bogomolov.aa.wordstrainer.model.GoogleSheet
import bogomolov.aa.wordstrainer.repository.GoogleSheetsRepository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GoogleSheetsViewModel
@Inject constructor(private val repository: GoogleSheetsRepository) : ViewModel() {
    val sheetsLiveData = MutableLiveData<List<GoogleSheet>>()

    fun loadSheets() {
        viewModelScope.launch(Dispatchers.IO) {
            sheetsLiveData.postValue(repository.getAllSheets())
        }
    }

    fun createGoogleSheet(name: String) {
        setSetting(repository.context, GOOGLE_SHEET_NAME, name)
        setSetting(repository.context, USE_GOOGLE_SHEET, true)
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.createSpreadsheet(name)
            if (id.isNullOrEmpty()) {
                setSetting(repository.context, USE_GOOGLE_SHEET, false)
            } else {
                setSetting(repository.context, GOOGLE_SHEET_ID, id)
            }
        }
    }
}