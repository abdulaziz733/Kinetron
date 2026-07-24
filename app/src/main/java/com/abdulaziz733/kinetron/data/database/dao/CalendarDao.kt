package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.CalendarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendars ORDER BY name ASC")
    fun getAllCalendars(): Flow<List<CalendarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendars(calendars: List<CalendarEntity>)

    @Query("SELECT COUNT(*) FROM calendars")
    suspend fun getCalendarsCount(): Int

    @Query("DELETE FROM calendars")
    suspend fun clearAllCalendars()
}
