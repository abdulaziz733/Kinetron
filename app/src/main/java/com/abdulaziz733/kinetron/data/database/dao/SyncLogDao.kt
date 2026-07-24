package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.SyncLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncLogDao {
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentSyncLogs(): Flow<List<SyncLogEntity>>

    @Insert
    suspend fun insertSyncLog(syncLog: SyncLogEntity)

    @Query("DELETE FROM sync_logs")
    suspend fun clearAllSyncLogs()
}
