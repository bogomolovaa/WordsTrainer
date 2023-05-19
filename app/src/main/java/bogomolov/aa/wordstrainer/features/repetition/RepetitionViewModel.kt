package bogomolov.aa.wordstrainer.features.repetition

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.domain.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepetitionViewModel
@Inject constructor(private val repository: Repository) : ViewModel() {
    var counterLiveData = MutableLiveData(0)
    var nextWordLiveData = MutableLiveData<Word>()

    init {
        nextWord()
    }

    fun right() {
        updateRank(1)
        nextWord()
    }

    fun wrong() {
        updateRank(-1)
        nextWord()
    }

    private fun nextWord() {
        viewModelScope.launch(Dispatchers.IO) {
            nextWordLiveData.postValue(repository.nextWord())
        }
    }

    private fun updateRank(delta: Int) {
        counterLiveData.value = counterLiveData.value!! + 1
        val word = nextWordLiveData.value
        if (word != null)
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateRank(word, delta)
            }
    }
}