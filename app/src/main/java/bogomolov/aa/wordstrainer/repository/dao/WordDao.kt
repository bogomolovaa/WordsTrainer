package bogomolov.aa.wordstrainer.repository.dao

import androidx.room.*
import bogomolov.aa.wordstrainer.repository.entity.Word

@Dao
interface WordDao {

    @Insert
    fun addWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWords(word: List<Word>)

    @Query("select * from Word where direction = :direction")
    fun loadAll(direction: String): List<Word>

    @Query("update Word set rank = :rank where id = :id")
    fun updateRank(id: Int, rank: Int)

    @Query("DELETE FROM Word")
    fun deleteAll()
}