package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey val id: String, // Message ID from Gmail or Outlook API to prevent duplicates
    val source: String, // "GMAIL" or "OUTLOOK"
    val subject: String,
    val sender: String,
    val body: String,
    val bodyHtml: String? = null,
    val dateReceived: Long, // Epoch ms when email was received
    val syncDate: Long // Epoch ms when local sync occurred
)
