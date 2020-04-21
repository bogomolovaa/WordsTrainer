package bogomolov.aa.wordstrainer.repository

import android.content.Context
import android.util.Log
import bogomolov.aa.wordstrainer.android.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.model.WordsRanger
import bogomolov.aa.wordstrainer.repository.entity.Word
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val MAX_RANK = 10

abstract class Repository(
    private val context: Context,
    private val translateProvider: YandexTranslateProvider
) {

    val words: MutableList<Word> = ArrayList()
    protected var wordsMap: MutableMap<String, Word> = HashMap()
    private var wordsRanger: WordsRanger? = null


    fun translate(text: String): Word? {
        var word = wordsMap[text]
        if (word == null) {
            word = translateProvider.translate(text)
            if (word != null) {
                word.direction = getSetting(context, TRANSLATION_DIRECTION)
                addWord(word)
                words.add(word)
                wordsMap[text] = word
                wordsRanger?.addWord(word)
            }
        } else {
            word.rank -= 1
            update(word)
        }
        return word
    }

    fun nextWord() = wordsRanger?.nextWord()

    fun deleteWord(word: Word) {
        word.deleted = 1
        delete(word)
        wordsRanger?.deleteWord(word)
    }

    fun updateRank(word: Word, delta: Int) {
        wordsRanger?.deleteWord(word)
        word.rank += delta
        update(word)
    }

    protected abstract fun delete(word: Word)
    protected abstract fun update(word: Word)
    protected abstract fun addWord(word: Word)
    protected abstract fun loadAllWords(): List<Word>

    fun initWords() {
        words.clear()
        wordsMap.clear()
        words.addAll(loadAllWords())
        for (word in words) wordsMap[word.word] = word
        updateWordsRanger()
    }

    protected fun updateWordsRanger() {
        wordsRanger = WordsRanger(words)
    }

}