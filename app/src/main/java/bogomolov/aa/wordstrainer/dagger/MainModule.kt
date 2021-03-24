package bogomolov.aa.wordstrainer.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import bogomolov.aa.wordstrainer.android.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.android.getSetting
import bogomolov.aa.wordstrainer.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule {

    @Provides
    fun providesContext(application: Application): Context = application

    @Singleton
    @Provides
    fun providesAppDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun providesRepository(
        application: Application,
        googleSheetsRepository: GoogleSheetsRepository,
        roomRepository: RoomRepository
    ): Repository {
        val useGoogleSheet = getSetting<Boolean>(application, USE_GOOGLE_SHEET)!!
        return if (useGoogleSheet) googleSheetsRepository else roomRepository
    }

}