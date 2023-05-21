package bogomolov.aa.wordstrainer.repository

import android.annotation.SuppressLint
import android.content.Context
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.domain.WordsRank
import bogomolov.aa.wordstrainer.features.shared.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.features.shared.getSetting
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class Repository(
    private val context: Context,
) {
    val words: MutableList<Word> = ArrayList()
    protected var wordsMap: MutableMap<String, Word> = HashMap()
    private var wordsRank: WordsRank? = null

    @SuppressLint("CheckResult")
    fun translate(text: String, translateWord: Single<Word>) = Single.create<Word> { emitter ->
        wordsMap[text]?.let {
            it.rank--
            update(it).subscribe {
                emitter.onSuccess(it)
            }
        } ?: translateWord.subscribe { word ->
            addWord(word, getSetting(context, TRANSLATION_DIRECTION)).subscribe {
                words.add(word)
                wordsMap[text] = word
                wordsRank?.addWord(word)
                emitter.onSuccess(word)
            }
        }
    }.subscribeOn(Schedulers.io())

    fun nextWord() = wordsRank?.nextWord()

    fun updateRank(word: Word, delta: Int): Completable = Completable.create {
        wordsRank?.deleteWord(word)
        word.rank += delta
        update(word)
        it.onComplete()
    }.subscribeOn(Schedulers.io())

    protected abstract fun update(word: Word): Completable
    protected abstract fun addWord(word: Word, direction: String?): Completable
    protected abstract fun loadAllWords(): Single<List<Word>>

    @SuppressLint("CheckResult")
    fun initWords() {
        words.clear()
        wordsMap.clear()
        loadAllWords().observeOn(Schedulers.io()).subscribe { loadedWords ->
            words.addAll(loadedWords)
            for (word in words) wordsMap[word.word] = word
            updateWordsRanger()
        }
    }

    protected fun updateWordsRanger() {
        wordsRank = WordsRank(words)
    }
}