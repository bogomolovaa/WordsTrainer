package bogomolov.aa.wordstrainer.repository.dao

import androidx.room.Dao
import androidx.room.Query
import bogomolov.aa.wordstrainer.repository.entity.Word

@Dao
interface WordDao {

    @Query("select * from Word")
    fun loadAll(): List<Word>
}