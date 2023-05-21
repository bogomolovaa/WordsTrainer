package bogomolov.aa.wordstrainer.features.google_sheets

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bogomolov.aa.wordstrainer.features.shared.GOOGLE_SHEET_NAME
import bogomolov.aa.wordstrainer.features.shared.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.features.shared.setSetting
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class GoogleSheetsViewModel
@Inject constructor(private val repository: GoogleSheetsRepository) : ViewModel() {
    val sheetsLiveData = MutableLiveData<List<GoogleSheet>>()

    @SuppressLint("CheckResult")
    fun loadSheets() {
        repository.getAllSheets().observeOn(AndroidSchedulers.mainThread()).subscribe { sheets ->
            sheetsLiveData.value = sheets
        }
    }

    @SuppressLint("CheckResult")
    fun createGoogleSheet(name: String) {
        setSetting(repository.context, GOOGLE_SHEET_NAME, name)
        setSetting(repository.context, USE_GOOGLE_SHEET, true)
        repository.createSpreadsheet(name).observeOn(Schedulers.computation()).subscribe { id ->
            if (id.isNullOrEmpty()) {
                setSetting(repository.context, USE_GOOGLE_SHEET, false)
            } else {
                setSetting(repository.context, GOOGLE_SHEET_ID, id)
                onSheetSelected()
            }
        }
    }

    fun onSheetSelected() {
        repository.initWords()
    }
}