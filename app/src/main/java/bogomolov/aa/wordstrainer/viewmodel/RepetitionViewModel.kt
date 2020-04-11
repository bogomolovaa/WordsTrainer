package bogomolov.aa.wordstrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import bogomolov.aa.wordstrainer.repository.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepetitionViewModel
@Inject constructor(private val repository: Repository) : ViewModel() {
    var lastWord: Word? = null

    fun deleteWord() {
        val word = lastWord
        if (word != null)
            viewModelScope.launch(Dispatchers.IO) { repository.deleteWord(word) }
    }

    fun nextWord(): Word? {
        lastWord = repository.nextWord()
        return lastWord
    }

    fun right() {
        val word = lastWord
        if (word != null)
            viewModelScope.launch(Dispatchers.IO) { repository.updateRank(word, 1) }
    }

    fun wrong() {
        val word = lastWord
        if (word != null)
            viewModelScope.launch(Dispatchers.IO) { repository.updateRank(word, -1) }
    }
}