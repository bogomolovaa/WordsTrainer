package bogomolov.aa.wordstrainer.domain

import bogomolov.aa.wordstrainer.repository.entity.Word
import com.google.common.primitives.Ints.max
import java.util.*
import kotlin.math.min

class WordsRank(words: List<Word>) {
    private val rankMap = HashMap<Int, RankList>()
    private var minRank = 0
    private var maxRank = 0

    init {
        for (word in words.filter { it.deleted == 0 }) addWord(word)
    }

    fun deleteWord(word: Word) {
        rankMap[word.rank]?.words?.remove(word)
    }

    fun addWord(word: Word) {
        val rankList = rankMap[word.rank] ?: RankList(word.rank).also { rankMap[word.rank] = it }
        rankList.words += word
        minRank = min(word.rank, minRank)
        maxRank = max(word.rank, maxRank)
    }

    fun nextWord(): Word? {
        for (rank in minRank..maxRank) {
            val rankList = rankMap[rank]
            if (rankList != null && rankList.words.isNotEmpty()) {
                return if (rankList.words.size > 1) {
                    val nextId = Random().nextInt(rankList.words.size)
                    rankList.words[nextId]
                    rankList.words.removeAt(nextId)
                } else {
                    rankMap.remove(rank)
                    rankList.words.first()
                }
            }
        }
        return null
    }
}

private data class RankList(val rank: Int, val words: MutableList<Word> = mutableListOf())