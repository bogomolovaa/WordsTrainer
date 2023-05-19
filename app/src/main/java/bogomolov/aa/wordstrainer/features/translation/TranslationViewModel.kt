package bogomolov.aa.wordstrainer.features.translation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.YandexTranslateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslationViewModel
@Inject constructor(
    private val repository: Repository,
    private val translateProvider: YandexTranslateProvider
) : ViewModel() {
    val translationLiveData = MutableLiveData<bogomolov.aa.wordstrainer.domain.Word>()

    fun translate(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            translationLiveData.postValue(repository.translate(text) {
                translateProvider.translate(text)
            })
        }
    }
}