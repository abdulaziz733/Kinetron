package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val syncType: String, // e.g. "CALL_LOG", "CONTACT", "CALENDAR", "GMAIL", "OUTLOOK"
    val lastSyncTimestamp: Long // Epoch ms of last successful sync
)
