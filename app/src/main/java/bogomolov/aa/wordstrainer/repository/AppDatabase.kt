package bogomolov.aa.wordstrainer.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import bogomolov.aa.wordstrainer.repository.dao.WordDao
import bogomolov.aa.wordstrainer.domain.Word
import bogomolov.aa.wordstrainer.repository.entity.WordEntity

const val DB_NAME = "words_db"

@Database(entities = [WordEntity::class], version = 11)
abstract class AppDatabase: RoomDatabase() {

    abstract fun wordsDao(): WordDao

}