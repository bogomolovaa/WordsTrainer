package bogomolov.aa.wordstrainer.repository

import android.util.Log
import bogomolov.aa.wordstrainer.repository.entity.Word
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val MAX_RANK = 3

abstract class Repository(private val translateProvider: YandexTranslateProvider) {

    protected val words: MutableList<Word> = ArrayList()
    private var wordsMap: MutableMap<String, Word> = HashMap()


    fun translate(text: String): Word? {
        var word = wordsMap[text]
        if (word == null) {
            word = translateProvider.translate(text)
            if (word != null) {
                wordsMap[text] = word
                words.add(word)
                addWord(word)
            }
        } else {
            updateRank(word, -1)
        }
        return word
    }

    fun nextWord(): Word? {
        val wordsToRemember = ArrayList<Word>()
        for (word in words) if (word.rank < MAX_RANK)
            wordsToRemember.add(word)
        if (wordsToRemember.size == 0) return null
        val nextId = Random().nextInt(wordsToRemember.size)
        return wordsToRemember[nextId]
    }

    abstract fun updateRank(word: Word, delta: Int)

    protected abstract fun addWord(word: Word)
    protected abstract fun loadAllWords(): List<Word>

    fun initWords() {
        words.clear()
        wordsMap.clear()
        words.addAll(loadAllWords())
        Log.i("test", "loaded words ${words.size}")
        for (word in words) {
            Log.i("test", "$word")
            wordsMap[word.word] = word
        }
    }


}