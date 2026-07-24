package com.abdulaziz733.kinetron.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: Long, // Device Contact ID to avoid duplicates
    val lookupKey: String?,
    val name: String,
    val phone: String?,
    val email: String?,
    val lastUpdatedTimestamp: Long, // ContactsContract.RawContacts.CONTACT_LAST_UPDATED_TIMESTAMP
    val syncDate: Long // Epoch ms when local sync occurred
)
