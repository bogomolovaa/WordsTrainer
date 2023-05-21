package bogomolov.aa.wordstrainer.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Word")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val word: String,
    val translation: String,
    var rank: Int = 0,
    val json: String? = null,
    var direction: String? = null
) {
    fun toWord() =
        bogomolov.aa.wordstrainer.domain.Word(
            id = id,
            word = word,
            translation = translation,
            rank = rank,
            json = json
        )

    companion object {
        fun toWordEntity(word: bogomolov.aa.wordstrainer.domain.Word, direction: String?) =
            WordEntity(
                id = word.id,
                word = word.word,
                translation = word.translation,
                rank = word.rank,
                json = word.json,
                direction = direction
            )
    }
}