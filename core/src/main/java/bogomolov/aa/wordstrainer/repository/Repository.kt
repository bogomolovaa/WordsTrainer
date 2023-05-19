package bogomolov.aa.wordstrainer.repository

import android.content.Context
import bogomolov.aa.wordstrainer.features.shared.TRANSLATION_DIRECTION
import bogomolov.aa.wordstrainer.features.shared.getSetting

abstract class Repository(
    private val context: Context,
) {
    val words: MutableList<bogomolov.aa.wordstrainer.domain.Word> = ArrayList()
    protected var wordsMap: MutableMap<String, bogomolov.aa.wordstrainer.domain.Word> = HashMap()
    private var wordsRank: bogomolov.aa.wordstrainer.domain.WordsRank? = null

    fun translate(
        text: String,
        translate: (String) -> bogomolov.aa.wordstrainer.domain.Word?
    ): bogomolov.aa.wordstrainer.domain.Word? {
        var word = wordsMap[text]
        if (word == null) {
            word = translate(text)
            if (word != null) {
                addWord(word, getSetting(context, TRANSLATION_DIRECTION))
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

    fun updateRank(word: bogomolov.aa.wordstrainer.domain.Word, delta: Int) {
        wordsRank?.deleteWord(word)
        word.rank += delta
        update(word)
    }

    protected abstract fun update(word: bogomolov.aa.wordstrainer.domain.Word)
    protected abstract fun addWord(word: bogomolov.aa.wordstrainer.domain.Word, direction: String?)
    protected abstract fun loadAllWords(): List<bogomolov.aa.wordstrainer.domain.Word>

    fun initWords() {
        words.clear()
        wordsMap.clear()
        words.addAll(loadAllWords())
        for (word in words) wordsMap[word.word] = word
        updateWordsRanger()
    }

    protected fun updateWordsRanger() {
        wordsRank = bogomolov.aa.wordstrainer.domain.WordsRank(words)
    }
}