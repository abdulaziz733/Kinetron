package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey val id: Long, // Device Call Log ID to avoid duplicates
    val number: String,
    val name: String?,
    val type: Int, // CallLog.Calls.INCOMING_TYPE, OUTGOING_TYPE, MISSED_TYPE, etc.
    val date: Long, // Epoch ms when call was made
    val duration: Long, // Duration in seconds
    val syncDate: Long // Epoch ms when local sync occurred
)
