package bogomolov.aa.wordstrainer.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Word (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val word: String,
    val translation: String,
    val rank: Int,
    val json: String
){
    override fun hashCode() : Int = id.toString().hashCode()
}