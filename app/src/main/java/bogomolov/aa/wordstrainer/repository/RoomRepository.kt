package bogomolov.aa.wordstrainer.repository

import android.content.Context
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.features.shared.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.features.shared.getSetting
import bogomolov.aa.wordstrainer.repository.entity.WordEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val context: Context,
    private val db: AppDatabase
) : Repository(context) {

    init {
        initWords()
    }

    override fun update(word: Word) = Completable.create {
        db.wordsDao().updateRank(word.id, word.rank)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    override fun addWord(word: Word, direction: String?) = Completable.create {
        word.id = db.wordsDao().addWord(WordEntity.toWordEntity(word, direction)).toInt()
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    override fun loadAllWords(): Single<List<Word>> {
        val direction = getSetting<String>(context, TRANSLATION_DIRECTION) ?: ""
        return db.wordsDao().loadAll(direction).map { it.map { it.toWord() } }
            .subscribeOn(Schedulers.io())
    }

    fun import(words: List<Word>) = Completable.create {
        val direction = getSetting<String>(
            context,
            TRANSLATION_DIRECTION
        )
        db.wordsDao().deleteAll()
        db.wordsDao().addWords(words.map { WordEntity.toWordEntity(it, direction).copy(id = 0) })
        initWords()
        it.onComplete()
    }.subscribeOn(Schedulers.io())
}