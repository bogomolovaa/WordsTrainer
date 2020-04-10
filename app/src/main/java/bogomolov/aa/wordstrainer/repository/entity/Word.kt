package bogomolov.aa.wordstrainer.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val word: String,
    val translation: String,
    var rank: Int = 0,
    val json: String,
    var direction: String? = null
){

    override fun toString() = "$id $word $translation"

    override fun hashCode() : Int = id.toString().hashCode()
}