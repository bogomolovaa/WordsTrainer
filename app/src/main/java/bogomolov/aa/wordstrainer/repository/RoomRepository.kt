package bogomolov.aa.wordstrainer.repository

import android.content.Context
import bogomolov.aa.wordstrainer.features.shared.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.features.shared.getSetting
import bogomolov.aa.wordstrainer.repository.entity.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val context: Context,
    private val db: AppDatabase
) : Repository(context) {

    init {
        GlobalScope.launch(Dispatchers.IO) {
            initWords()
        }
    }

    override fun update(word: bogomolov.aa.wordstrainer.domain.Word) {
        db.wordsDao().updateRank(word.id, word.rank)
    }

    override fun addWord(word: bogomolov.aa.wordstrainer.domain.Word, direction: String?) {
        word.id = db.wordsDao().addWord(WordEntity.toWordEntity(word, direction)).toInt()
    }

    override fun loadAllWords(): List<bogomolov.aa.wordstrainer.domain.Word> {
        val direction = getSetting<String>(
            context,
            TRANSLATION_DIRECTION
        ) ?: ""
        return db.wordsDao().loadAll(direction).map { it.toWord() }
    }

    fun import(words: List<bogomolov.aa.wordstrainer.domain.Word>) {
        val direction = getSetting<String>(
            context,
            TRANSLATION_DIRECTION
        )
        db.wordsDao().deleteAll()
        db.wordsDao().addWords(words.map { WordEntity.toWordEntity(it, direction).copy(id = 0) })
        initWords()
    }
}