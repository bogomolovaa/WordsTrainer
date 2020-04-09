package bogomolov.aa.wordstrainer.repository

import android.util.Log
import bogomolov.aa.wordstrainer.repository.entity.Word
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository
@Inject constructor(
    private val db: AppDatabase,
    translateProvider: YandexTranslateProvider
) : Repository(translateProvider) {

    override fun updateRank(word: Word, delta: Int) {
        word.rank += delta
        db.wordsDao().updateRank(word.id, word.rank)
    }

    override fun addWord(word: Word) {
        word.id = db.wordsDao().addWord(word).toInt()
    }

    override fun loadAllWords(): List<Word> {
        Log.i("test", "RoomRepository loadAllWords()")
        return db.wordsDao().loadAll()
    }

}