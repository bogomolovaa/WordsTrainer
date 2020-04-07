package bogomolov.aa.wordstrainer.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import bogomolov.aa.wordstrainer.repository.AppDatabase
import bogomolov.aa.wordstrainer.repository.DB_NAME
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class MainModule {
    @Binds
    abstract fun bindsRepository(repository: RepositoryImpl): Repository

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesAppDatabase(application: Application): AppDatabase =
            Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration().build()

        @JvmStatic
        @Provides
        fun providesContext(application: Application): Context = application
    }
}