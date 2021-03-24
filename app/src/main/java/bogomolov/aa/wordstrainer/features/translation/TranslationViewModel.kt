package bogomolov.aa.wordstrainer.features.translation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import bogomolov.aa.wordstrainer.repository.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslationViewModel
@Inject constructor(private val repository: Repository) : ViewModel() {
    val translationLiveData = MutableLiveData<Word>()

    fun translate(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            translationLiveData.postValue(repository.translate(text))
        }
    }

}