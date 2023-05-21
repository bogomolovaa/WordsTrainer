package bogomolov.aa.wordstrainer.features.repetition

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.repository.Repository
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
        nextWordLiveData.value = repository.nextWord()
    }

    private fun updateRank(delta: Int) {
        counterLiveData.value = counterLiveData.value!! + 1
        nextWordLiveData.value?.let { repository.updateRank(it, delta).subscribe() }
    }
}