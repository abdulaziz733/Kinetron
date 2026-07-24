package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startTime DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE calendarId = :calendarId ORDER BY startTime DESC")
    fun getEventsForCalendar(calendarId: Long): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventsCount(): Int

    @Query("DELETE FROM events")
    suspend fun clearAllEvents()
}
