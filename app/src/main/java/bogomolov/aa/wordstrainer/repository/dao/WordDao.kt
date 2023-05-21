package bogomolov.aa.wordstrainer.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bogomolov.aa.wordstrainer.repository.entity.WordEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface WordDao {

    @Insert
    fun addWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWords(word: List<WordEntity>)

    @Query("select * from Word where direction = :direction")
    fun loadAll(direction: String): Single<List<WordEntity>>

    @Query("update Word set rank = :rank where id = :id")
    fun updateRank(id: Int, rank: Int)

    @Query("DELETE FROM Word")
    fun deleteAll()
}