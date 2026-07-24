package com.abdulaziz733.kinetron.di

import android.content.Context
import androidx.room.Room
import com.abdulaziz733.kinetron.data.database.KinetronDatabase
import com.abdulaziz733.kinetron.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KinetronDatabase {
        return Room.databaseBuilder(
            context,
            KinetronDatabase::class.java,
            "kinetron_cache_db"
        )
        .fallbackToDestructiveMigration(true)
        .build()
    }

    @Provides
    fun provideCallLogDao(db: KinetronDatabase): CallLogDao = db.callLogDao()

    @Provides
    fun provideContactDao(db: KinetronDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideCalendarDao(db: KinetronDatabase): CalendarDao = db.calendarDao()

    @Provides
    fun provideEventDao(db: KinetronDatabase): EventDao = db.eventDao()

    @Provides
    fun provideEmailDao(db: KinetronDatabase): EmailDao = db.emailDao()

    @Provides
    fun provideLocationDao(db: KinetronDatabase): LocationDao = db.locationDao()

    @Provides
    fun provideSyncMetadataDao(db: KinetronDatabase): SyncMetadataDao = db.syncMetadataDao()

    @Provides
    fun provideSyncLogDao(db: KinetronDatabase): SyncLogDao = db.syncLogDao()
}
