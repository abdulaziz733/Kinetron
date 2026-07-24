package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendars")
data class CalendarEntity(
    @PrimaryKey val id: Long, // Device Calendar ID
    val name: String,
    val accountName: String,
    val accountType: String,
    val syncDate: Long
)
