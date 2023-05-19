package bogomolov.aa.wordstrainer.features.google_sheets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.features.shared.GOOGLE_SHEET_NAME
import bogomolov.aa.wordstrainer.features.shared.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.features.shared.setSetting
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
                onSheetSelected()
            }
        }
    }

    fun onSheetSelected(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.initWords()
        }
    }
}