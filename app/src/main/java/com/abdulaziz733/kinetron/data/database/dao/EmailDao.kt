package com.abdulaziz733.kinetron.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz733.kinetron.data.database.entity.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails ORDER BY dateReceived DESC")
    fun getAllEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE source = :source ORDER BY dateReceived DESC")
    fun getEmailsBySource(source: String): Flow<List<EmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmails(emails: List<EmailEntity>)

    @Query("SELECT COUNT(*) FROM emails")
    suspend fun getEmailsCount(): Int

    @Query("SELECT COUNT(*) FROM emails WHERE source = :source")
    suspend fun getEmailsCountBySource(source: String): Int

    @Query("DELETE FROM emails")
    suspend fun clearAllEmails()

    @Query("DELETE FROM emails WHERE source = :source")
    suspend fun deleteEmailsBySource(source: String)
}
