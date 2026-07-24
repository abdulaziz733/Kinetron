package com.abdulaziz733.kinetron.data.repository

import com.abdulaziz733.kinetron.data.database.entity.*
import kotlinx.coroutines.flow.Flow

interface DeviceDataRepository {
    // Local DB Observables
    fun getCallLogs(): Flow<List<CallLogEntity>>
    fun getContacts(): Flow<List<ContactEntity>>
    fun getCalendars(): Flow<List<CalendarEntity>>
    fun getEvents(): Flow<List<EventEntity>>
    fun getEmails(): Flow<List<EmailEntity>>
    fun getLocations(): Flow<List<LocationEntity>>
    fun getLatestLocation(): Flow<LocationEntity?>
    fun getSyncMetadata(): Flow<List<SyncMetadataEntity>>

    // Gmail Account (Android OAuth — no token storage needed)
    fun getGmailAccountEmail(): String?
    fun saveGmailAccountEmail(email: String?)

    // Outlook token (coming soon)
    fun getOutlookToken(): String?
    fun saveOutlookToken(token: String?)

    // Content Provider Mutator/Creator
    suspend fun createLocalCalendar(name: String): Boolean
    suspend fun createContact(name: String, phone: String, email: String): Boolean
    suspend fun createEvent(calendarId: Long, title: String, description: String?, startTime: Long, endTime: Long): Boolean

    // Sync Operations
    suspend fun syncCallLogs(): Result<Int>
    suspend fun syncContacts(): Result<Int>
    suspend fun syncCalendarsAndEvents(): Result<Int>
    suspend fun syncEmails(): Result<Int>
    suspend fun syncAll(isBackground: Boolean = false): Map<String, Result<Int>>

    // Location capturing
    suspend fun captureLocation(): Result<LocationEntity>

    // Permission check status mapping
    fun getPermissionStatuses(): Map<String, Boolean>

    // First Launch Onboarding
    fun isFirstLaunchOnboarding(): Boolean
    fun setFirstLaunchOnboardingCompleted()

    // Sync Logs and Scheduler config
    fun isSchedulerEnabled(): Boolean
    fun setSchedulerEnabled(enabled: Boolean)
    fun getSyncLogs(): Flow<List<SyncLogEntity>>
    suspend fun clearSyncLogs()
    suspend fun deleteEmailsBySource(source: String)
    suspend fun deleteSyncMetadataByType(syncType: String)
}
