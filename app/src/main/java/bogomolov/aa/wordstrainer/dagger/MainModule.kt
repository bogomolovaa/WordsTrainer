package bogomolov.aa.wordstrainer.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.features.google_sheets.GoogleSheetsRepository
import bogomolov.aa.wordstrainer.features.shared.USE_GOOGLE_SHEET
import bogomolov.aa.wordstrainer.features.shared.getSetting
import bogomolov.aa.wordstrainer.repository.AppDatabase
import bogomolov.aa.wordstrainer.repository.DB_NAME
import bogomolov.aa.wordstrainer.repository.Repository
import bogomolov.aa.wordstrainer.repository.RoomRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule {

    @Provides
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideGoogleSheetsRepository(application: Application): GoogleSheetsRepository {
        return GoogleSheetsRepository(application, application.getString(R.string.app_name))
    }

    @Singleton
    @Provides
    fun provideRepository(
        application: Application,
        googleSheetsRepository: GoogleSheetsRepository,
        roomRepository: RoomRepository
    ): Repository {
        val useGoogleSheet = getSetting<Boolean>(
            application,
            USE_GOOGLE_SHEET
        )!!
        return if (useGoogleSheet) googleSheetsRepository else roomRepository
    }
}