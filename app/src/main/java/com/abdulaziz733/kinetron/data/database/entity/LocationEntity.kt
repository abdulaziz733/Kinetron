package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long, // Epoch ms when coordinate was captured
    val syncDate: Long // Epoch ms when saved locally
)
