package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.CallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY date DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLogs(callLogs: List<CallLogEntity>)

    @Query("SELECT COUNT(*) FROM call_logs")
    suspend fun getCallLogsCount(): Int

    @Query("DELETE FROM call_logs")
    suspend fun clearAllCallLogs()

    @Query("DELETE FROM call_logs WHERE date >= :startOfYear AND id NOT IN (:activeIds)")
    suspend fun deleteCallLogsNotIn(activeIds: List<Long>, startOfYear: Long)
}
