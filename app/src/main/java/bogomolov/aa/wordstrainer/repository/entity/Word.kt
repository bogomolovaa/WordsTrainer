package bogomolov.aa.wordstrainer.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val word: String,
    val translation: String,
    var rank: Int = 0,
    val json: String,
    var direction: String? = null,
    var deleted: Int = 0
) {

    override fun toString() = "$id $word $translation"

    override fun hashCode(): Int = id.toString().hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return id == (other as Word).id
    }
}