package bogomolov.aa.wordstrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslationViewModel
@Inject constructor(private val repository: Repository) : ViewModel() {

    fun loadWords(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.initWords()
        }
    }

}