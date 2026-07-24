package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactsCount(): Int

    @Query("DELETE FROM contacts")
    suspend fun clearAllContacts()

    @Query("DELETE FROM contacts WHERE lastUpdatedTimestamp >= :startOfYear AND id NOT IN (:activeIds)")
    suspend fun deleteContactsNotIn(activeIds: List<Long>, startOfYear: Long)
}
