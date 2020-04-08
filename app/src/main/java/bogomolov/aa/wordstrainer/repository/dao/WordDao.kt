package bogomolov.aa.wordstrainer.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import bogomolov.aa.wordstrainer.repository.entity.Word

@Dao
interface WordDao {

    @Insert
    fun addWord(word: Word): Long

    @Query("select * from Word")
    fun loadAll(): List<Word>

    @Query("update Word set rank = :rank where id = :id")
    fun updateRank(id: Int, rank: Int)
}