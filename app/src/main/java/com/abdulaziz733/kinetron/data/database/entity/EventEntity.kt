package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: Long, // Device Event ID
    val calendarId: Long,
    val title: String,
    val description: String?,
    val startTime: Long, // Epoch ms
    val endTime: Long, // Epoch ms
    val syncDate: Long // Epoch ms when local sync occurred
)
