package bogomolov.aa.wordstrainer.features.translation

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.YandexTranslateProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class TranslationViewModel
@Inject constructor(
    private val repository: Repository,
    private val translateProvider: YandexTranslateProvider
) : ViewModel() {
    val translationLiveData = MutableLiveData<Word>()

    @SuppressLint("CheckResult")
    fun translate(text: String) {
        repository.translate(text, translateProvider.translate(text)).observeOn(AndroidSchedulers.mainThread()).subscribe { word ->
            translationLiveData.value = word
        }
    }
}