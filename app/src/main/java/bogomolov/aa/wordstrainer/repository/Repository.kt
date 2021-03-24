package bogomolov.aa.wordstrainer.repository

import android.content.Context
import bogomolov.aa.wordstrainer.android.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.domain.WordsRank
import bogomolov.aa.wordstrainer.repository.entity.Word

abstract class Repository(
    private val context: Context,
    private val translateProvider: YandexTranslateProvider
) {
    val words: MutableList<Word> = ArrayList()
    protected var wordsMap: MutableMap<String, Word> = HashMap()
    private var wordsRank: WordsRank? = null

    fun translate(text: String): Word? {
        var word = wordsMap[text]
        if (word == null) {
            word = translateProvider.translate(text)
            if (word != null) {
                word.direction = getSetting(context, TRANSLATION_DIRECTION)
                addWord(word)
                words.add(word)
                wordsMap[text] = word
                wordsRank?.addWord(word)
            }
        } else {
            word.rank -= 1
            update(word)
        }
        return word
    }

    fun nextWord() = wordsRank?.nextWord()

    fun updateRank(word: Word, delta: Int) {
        wordsRank?.deleteWord(word)
        word.rank += delta
        update(word)
    }

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
        wordsRank = WordsRank(words)
    }
}