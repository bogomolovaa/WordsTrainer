package bogomolov.aa.wordstrainer.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import bogomolov.aa.wordstrainer.android.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.repository.entity.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository
@Inject constructor(
    private val context: Context,
    private val db: AppDatabase,
    translateProvider: YandexTranslateProvider
) : Repository(context, translateProvider) {

    init {
        GlobalScope.launch(Dispatchers.IO) {
            initWords()
        }
    }

    override fun updateRank(word: Word, delta: Int) {
        word.rank += delta
        db.wordsDao().updateRank(word.id, word.rank)
    }

    override fun addWord(word: Word) {
        word.id = db.wordsDao().addWord(word).toInt()
    }

    override fun loadAllWords(): List<Word> {
        Log.i("test", "RoomRepository loadAllWords()")
        val direction = getSetting<String>(context, TRANSLATION_DIRECTION)
        return db.wordsDao().loadAll(direction!!)
    }

    fun addWords(words: List<Word>){
        db.wordsDao().addWords(words)
    }

}