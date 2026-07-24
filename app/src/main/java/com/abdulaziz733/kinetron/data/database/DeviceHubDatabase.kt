package com.abdulaziz733.kinetron.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abdulaziz733.kinetron.data.database.dao.*
import com.abdulaziz733.kinetron.data.database.entity.*

@Database(
    entities = [
        CallLogEntity::class,
        ContactEntity::class,
        CalendarEntity::class,
        EventEntity::class,
        EmailEntity::class,
        LocationEntity::class,
        SyncMetadataEntity::class,
        SyncLogEntity::class
    ],
    version = 3, // Increment version for EmailEntity bodyHtml column addition
    exportSchema = false
)
abstract class KinetronDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao
    abstract fun contactDao(): ContactDao
    abstract fun calendarDao(): CalendarDao
    abstract fun eventDao(): EventDao
    abstract fun emailDao(): EmailDao
    abstract fun locationDao(): LocationDao
    abstract fun syncMetadataDao(): SyncMetadataDao
    abstract fun syncLogDao(): SyncLogDao
}
