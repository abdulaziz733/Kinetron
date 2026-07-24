package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.SyncMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncMetadataDao {
    @Query("SELECT * FROM sync_metadata")
    fun getAllSyncMetadata(): Flow<List<SyncMetadataEntity>>

    @Query("SELECT lastSyncTimestamp FROM sync_metadata WHERE syncType = :syncType")
    suspend fun getLastSyncTimestamp(syncType: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncMetadata(syncMetadata: SyncMetadataEntity)

    @Query("DELETE FROM sync_metadata")
    suspend fun clearAllMetadata()

    @Query("DELETE FROM sync_metadata WHERE syncType = :syncType")
    suspend fun deleteSyncMetadataByType(syncType: String)
}
