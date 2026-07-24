package com.abdulaziz733.kinetron.data.repository

import android.Manifest
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.abdulaziz733.kinetron.data.database.KinetronDatabase
import com.abdulaziz733.kinetron.data.database.entity.*
import com.abdulaziz733.kinetron.data.network.service.GmailService
import com.abdulaziz733.kinetron.data.network.service.OutlookService
import com.abdulaziz733.kinetron.data.sync.SyncScheduler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceDataRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: KinetronDatabase,
    private val gmailService: GmailService,
    private val outlookService: OutlookService,
    private val fusedLocationClient: FusedLocationProviderClient
) : DeviceDataRepository {

    private val prefsName = "kinetron_prefs"
    private val sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    private val callLogDao = database.callLogDao()
    private val contactDao = database.contactDao()
    private val calendarDao = database.calendarDao()
    private val eventDao = database.eventDao()
    private val emailDao = database.emailDao()
    private val locationDao = database.locationDao()
    private val syncMetadataDao = database.syncMetadataDao()
    private val syncLogDao = database.syncLogDao()

    override fun getCallLogs(): Flow<List<CallLogEntity>> = callLogDao.getAllCallLogs()
    override fun getContacts(): Flow<List<ContactEntity>> = contactDao.getAllContacts()
    override fun getCalendars(): Flow<List<CalendarEntity>> = calendarDao.getAllCalendars()
    override fun getEvents(): Flow<List<EventEntity>> = eventDao.getAllEvents()
    override fun getEmails(): Flow<List<EmailEntity>> = emailDao.getAllEmails()
    override fun getLocations(): Flow<List<LocationEntity>> = locationDao.getAllLocations()
    override fun getLatestLocation(): Flow<LocationEntity?> = locationDao.getLatestLocation()
    override fun getSyncMetadata(): Flow<List<SyncMetadataEntity>> = syncMetadataDao.getAllSyncMetadata()

    override fun isSchedulerEnabled(): Boolean {
        return sharedPrefs.getBoolean("scheduler_enabled", true)
    }

    override fun setSchedulerEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("scheduler_enabled", enabled).apply()
        if (enabled) {
            SyncScheduler.schedulePeriodicSync(context)
        } else {
            SyncScheduler.cancelPeriodicSync(context)
        }
    }

    override fun getSyncLogs(): Flow<List<SyncLogEntity>> = syncLogDao.getRecentSyncLogs()

    override suspend fun clearSyncLogs() {
        syncLogDao.clearAllSyncLogs()
    }

    override suspend fun deleteEmailsBySource(source: String) {
        emailDao.deleteEmailsBySource(source)
    }

    override suspend fun deleteSyncMetadataByType(syncType: String) {
        syncMetadataDao.deleteSyncMetadataByType(syncType)
    }

    /** Gmail account email is the only persistent state needed — token refresh is handled by Play Services. */
    override fun getGmailAccountEmail(): String? = sharedPrefs.getString("gmail_account_email", null)
    override fun saveGmailAccountEmail(email: String?) {
        sharedPrefs.edit().putString("gmail_account_email", email).apply()
    }

    override fun getOutlookToken(): String? = sharedPrefs.getString("outlook_token", null)
    override fun saveOutlookToken(token: String?) {
        sharedPrefs.edit().putString("outlook_token", token).apply()
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun getPermissionStatuses(): Map<String, Boolean> {
        val statuses = mutableMapOf(
            "Call Log" to hasPermission(Manifest.permission.READ_CALL_LOG),
            "Calendar (Read)" to hasPermission(Manifest.permission.READ_CALENDAR),
            "Calendar (Write)" to hasPermission(Manifest.permission.WRITE_CALENDAR),
            "Contact (Read)" to hasPermission(Manifest.permission.READ_CONTACTS),
            "Contact (Write)" to hasPermission(Manifest.permission.WRITE_CONTACTS),
            "Location (Coarse)" to hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION),
            "Location (Fine)" to hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            statuses["Location (Background)"] = hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            statuses["Notifications"] = hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        }
        return statuses
    }

    override fun isFirstLaunchOnboarding(): Boolean {
        return sharedPrefs.getBoolean("is_first_launch_onboarding", true)
    }

    override fun setFirstLaunchOnboardingCompleted() {
        sharedPrefs.edit().putBoolean("is_first_launch_onboarding", false).apply()
    }

    override suspend fun createLocalCalendar(name: String): Boolean = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.WRITE_CALENDAR)) return@withContext false
        try {
            val uri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "Kinetron Local")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build()

            val values = ContentValues().apply {
                put(CalendarContract.Calendars.NAME, name)
                put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, name)
                put(CalendarContract.Calendars.ACCOUNT_NAME, "Kinetron Local")
                put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                put(CalendarContract.Calendars.OWNER_ACCOUNT, "kinetron@local.com")
                put(CalendarContract.Calendars.SYNC_EVENTS, 1)
                put(CalendarContract.Calendars.CALENDAR_COLOR, 0xFF008080.toInt()) // Tosca / Teal color
                put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
            }

            val resultUri = context.contentResolver.insert(uri, values)
            if (resultUri != null) {
                // Sync calendar list after creation
                syncCalendarsAndEvents()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun createContact(name: String, phone: String, email: String): Boolean = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.WRITE_CONTACTS)) return@withContext false
        try {
            val ops = ArrayList<ContentProviderOperation>()

            // 1. Raw contact insert
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build())

            // 2. Structured name insert
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build())

            // 3. Phone insert
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build())

            // 4. Email insert
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build())

            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            // Trigger contact sync to pull the newly created contact into Room
            syncContacts()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun createEvent(
        calendarId: Long,
        title: String,
        description: String?,
        startTime: Long,
        endTime: Long
    ): Boolean = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.WRITE_CALENDAR)) return@withContext false
        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startTime)
                put(CalendarContract.Events.DTEND, endTime)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            val resultUri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            if (resultUri != null) {
                syncCalendarsAndEvents()
                return@withContext true
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun syncCallLogs(): Result<Int> = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.READ_CALL_LOG)) {
            return@withContext Result.failure(SecurityException("Call Log permission denied"))
        }

        try {
            val lastSync = syncMetadataDao.getLastSyncTimestamp("CALL_LOG") ?: 0L
            val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )

            // YTD Constraint: start of current year
            val startOfYear = Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val syncStartTimestamp = maxOf(lastSync, startOfYear)

            // Delta sync combined with YTD constraint
            val selection = "${CallLog.Calls.DATE} >= ?"
            val selectionArgs = arrayOf(syncStartTimestamp.toString())
            val sortOrder = "${CallLog.Calls.DATE} ASC"

            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            val callLogs = mutableListOf<CallLogEntity>()
            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(CallLog.Calls._ID)
                val numCol = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val nameCol = c.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                val typeCol = c.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                val dateCol = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
                val durCol = c.getColumnIndexOrThrow(CallLog.Calls.DURATION)

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val number = c.getString(numCol) ?: "Unknown"
                    val name = c.getString(nameCol)
                    val type = c.getInt(typeCol)
                    val date = c.getLong(dateCol)
                    val duration = c.getLong(durCol)

                    callLogs.add(
                        CallLogEntity(
                            id = id,
                            number = number,
                            name = name,
                            type = type,
                            date = date,
                            duration = duration,
                            syncDate = System.currentTimeMillis()
                        )
                    )
                }
            }

            if (callLogs.isNotEmpty()) {
                callLogDao.insertCallLogs(callLogs)
            }

            // Deletion Sync: Query all active call log IDs in YTD
            val idCursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls._ID),
                "${CallLog.Calls.DATE} >= ?",
                arrayOf(startOfYear.toString()),
                null
            )
            val activeIds = mutableListOf<Long>()
            idCursor?.use { ic ->
                val idCol = ic.getColumnIndexOrThrow(CallLog.Calls._ID)
                while (ic.moveToNext()) {
                    activeIds.add(ic.getLong(idCol))
                }
            }
            if (activeIds.isEmpty()) {
                callLogDao.clearAllCallLogs()
            } else {
                callLogDao.deleteCallLogsNotIn(activeIds, startOfYear)
            }

            // Save new sync timestamp
            syncMetadataDao.insertSyncMetadata(
                SyncMetadataEntity("CALL_LOG", System.currentTimeMillis())
            )

            Result.success(callLogs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncContacts(): Result<Int> = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
            return@withContext Result.failure(SecurityException("Contacts permission denied"))
        }

        try {
            val lastSync = syncMetadataDao.getLastSyncTimestamp("CONTACT") ?: 0L

            // ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP is available in API 18+
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
            )

            // YTD Constraint: start of current year
            val startOfYear = Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val syncStartTimestamp = maxOf(lastSync, startOfYear)

            // Delta sync combined with YTD constraint
            val selection = "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} >= ?"
            val selectionArgs = arrayOf(syncStartTimestamp.toString())

            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )

            val contacts = mutableListOf<ContactEntity>()
            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                val keyCol = c.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)
                val nameCol = c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                val tsCol = c.getColumnIndexOrThrow(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val lookupKey = c.getString(keyCol)
                    val name = c.getString(nameCol) ?: "No Name"
                    val lastUpdated = c.getLong(tsCol)

                    // Fetch phone number
                    var phone: String? = null
                    val phoneCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id.toString()),
                        null
                    )
                    phoneCursor?.use { pc ->
                        if (pc.moveToFirst()) {
                            phone = pc.getString(0)
                        }
                    }

                    // Fetch email address
                    var email: String? = null
                    val emailCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                        "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                        arrayOf(id.toString()),
                        null
                    )
                    emailCursor?.use { ec ->
                        if (ec.moveToFirst()) {
                            email = ec.getString(0)
                        }
                    }

                    contacts.add(
                        ContactEntity(
                            id = id,
                            lookupKey = lookupKey,
                            name = name,
                            phone = phone,
                            email = email,
                            lastUpdatedTimestamp = lastUpdated,
                            syncDate = System.currentTimeMillis()
                        )
                    )
                }
            }

            if (contacts.isNotEmpty()) {
                contactDao.insertContacts(contacts)
            }

            // Deletion Sync: Query all active contact IDs in YTD
            val idCursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts._ID),
                "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} >= ?",
                arrayOf(startOfYear.toString()),
                null
            )
            val activeIds = mutableListOf<Long>()
            idCursor?.use { ic ->
                val idCol = ic.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                while (ic.moveToNext()) {
                    activeIds.add(ic.getLong(idCol))
                }
            }
            if (activeIds.isEmpty()) {
                contactDao.clearAllContacts()
            } else {
                contactDao.deleteContactsNotIn(activeIds, startOfYear)
            }

            // Save new sync timestamp
            syncMetadataDao.insertSyncMetadata(
                SyncMetadataEntity("CONTACT", System.currentTimeMillis())
            )

            Result.success(contacts.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncCalendarsAndEvents(): Result<Int> = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.READ_CALENDAR)) {
            return@withContext Result.failure(SecurityException("Calendar permission denied"))
        }

        try {
            val lastSync = syncMetadataDao.getLastSyncTimestamp("CALENDAR") ?: 0L

            // 1. Sync Visible Calendars (VISIBLE = 1 filters out unchecked/disabled accounts)
            val calProjection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE
            )

            val calCursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                calProjection,
                "${CalendarContract.Calendars.VISIBLE} = 1",
                null,
                null
            )

            val calendars = mutableListOf<CalendarEntity>()
            calCursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                val nameCol = c.getColumnIndexOrThrow(CalendarContract.Calendars.NAME)
                val accNameCol = c.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)
                val accTypeCol = c.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE)

                while (c.moveToNext()) {
                    calendars.add(
                        CalendarEntity(
                            id = c.getLong(idCol),
                            name = c.getString(nameCol) ?: "Local",
                            accountName = c.getString(accNameCol) ?: "Device",
                            accountType = c.getString(accTypeCol) ?: "Local",
                            syncDate = System.currentTimeMillis()
                        )
                    )
                }
            }

            // Clear old calendars and insert fresh set
            calendarDao.clearAllCalendars()
            if (calendars.isNotEmpty()) {
                calendarDao.insertCalendars(calendars)
            } else {
                eventDao.clearAllEvents()
                return@withContext Result.success(0)
            }

            // 2. Sync Events (Fetch all events in the current calendar year for YTD constraint)
            val eventProjection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
            )

            // YTD Constraint: start of current year
            val startOfYear = Calendar.getInstance().apply {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Only query events belonging to visible calendars
            val visibleCalIds = calendars.map { it.id }
            val inClause = visibleCalIds.joinToString(",") { "?" }
            val selection = "${CalendarContract.Events.CALENDAR_ID} IN ($inClause) AND ${CalendarContract.Events.DTSTART} >= ?"
            val selectionArgs = (visibleCalIds.map { it.toString() } + startOfYear.toString()).toTypedArray()

            var eventCursor: Cursor? = null
            try {
                eventCursor = context.contentResolver.query(
                    CalendarContract.Events.CONTENT_URI,
                    eventProjection,
                    selection,
                    selectionArgs,
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val events = mutableListOf<EventEntity>()
            val seenKeys = mutableSetOf<String>()

            eventCursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(CalendarContract.Events._ID)
                val calIdCol = c.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_ID)
                val titleCol = c.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
                val descCol = c.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)
                val startCol = c.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
                val endCol = c.getColumnIndexOrThrow(CalendarContract.Events.DTEND)

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val calId = c.getLong(calIdCol)
                    val title = c.getString(titleCol) ?: "No Title"
                    val desc = c.getString(descCol)
                    val start = c.getLong(startCol)
                    val end = c.getLong(endCol)

                    // De-duplicate based on Title + StartTime + EndTime to prevent double-synced holiday listings
                    val key = "$title|$start|$end"
                    if (!seenKeys.contains(key)) {
                        seenKeys.add(key)
                        events.add(
                            EventEntity(
                                id = id,
                                calendarId = calId,
                                title = title,
                                description = desc,
                                startTime = start,
                                endTime = end,
                                syncDate = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }

            eventDao.clearAllEvents()
            if (events.isNotEmpty()) {
                eventDao.insertEvents(events)
            }

            // Save new sync timestamp
            syncMetadataDao.insertSyncMetadata(
                SyncMetadataEntity("CALENDAR", System.currentTimeMillis())
            )

            Result.success(events.size + calendars.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncEmails(): Result<Int> = withContext(Dispatchers.IO) {
        // Fetch fresh token via Play Services — auto-refreshes if expired, no manual refresh needed
        val accountEmail = getGmailAccountEmail()
        val gmailToken = if (!accountEmail.isNullOrBlank()) {
            com.abdulaziz733.kinetron.data.network.OAuthHelper.getGmailAccessToken(context, accountEmail)
        } else null

        val outlookToken = getOutlookToken()

        val syncedEmails = mutableListOf<EmailEntity>()
        var gmailError: Throwable? = null
        var outlookError: Throwable? = null

        // YTD Constraint: start of current year
        val startOfYear = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 1. Sync Gmail
        if (!gmailToken.isNullOrBlank()) {
            try {
                val lastSyncMs = syncMetadataDao.getLastSyncTimestamp("GMAIL") ?: 0L
                val syncStartMs = maxOf(lastSyncMs, startOfYear)
                val lastSyncSec = syncStartMs / 1000L
                // Query "after:TIMESTAMP"
                val query = "after:$lastSyncSec"
                val response = gmailService.getMessages("Bearer $gmailToken", query)
                response.messages?.forEach { msgSummary ->
                    try {
                        val detail = gmailService.getMessageDetail("Bearer $gmailToken", msgSummary.id)
                        val headers = detail.payload?.headers
                        val subject = headers?.find { it.name.equals("Subject", true) }?.value ?: "No Subject"
                        val sender = headers?.find { it.name.equals("From", true) }?.value ?: "Unknown Sender"
                        
                        var bodyHtml: String? = extractGmailBody(detail.payload, preferHtml = true)
                        if (bodyHtml.isNullOrBlank()) {
                            bodyHtml = null
                        }
                        
                        syncedEmails.add(
                            EmailEntity(
                                id = detail.id,
                                source = "GMAIL",
                                subject = subject,
                                sender = sender,
                                body = detail.snippet ?: "",
                                bodyHtml = bodyHtml,
                                dateReceived = detail.internalDate,
                                syncDate = System.currentTimeMillis()
                            )
                        )
                    } catch (e: Exception) {
                        // ignore single message errors
                    }
                }
                syncMetadataDao.insertSyncMetadata(SyncMetadataEntity("GMAIL", System.currentTimeMillis()))
            } catch (e: Exception) {
                gmailError = e
            }
        }

        // 2. Sync Outlook
        if (!outlookToken.isNullOrBlank()) {
            try {
                val lastSyncMs = syncMetadataDao.getLastSyncTimestamp("OUTLOOK") ?: 0L
                val syncStartMs = maxOf(lastSyncMs, startOfYear)
                val dateString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(syncStartMs))
                // Filter "receivedDateTime ge TIMESTAMP"
                val filter = "receivedDateTime ge $dateString"
                val response = outlookService.getMessages("Bearer $outlookToken", filter)
                response.value?.forEach { msg ->
                    val sender = msg.from?.emailAddress?.let { "${it.name ?: ""} <${it.address ?: ""}>" } ?: "Unknown Sender"
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                    val dateReceived = try {
                        sdf.parse(msg.receivedDateTime ?: "")?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                    val bodyHtml = if (msg.body?.contentType?.contains("html", true) == true) msg.body.content else null
                    syncedEmails.add(
                        EmailEntity(
                            id = msg.id,
                            source = "OUTLOOK",
                            subject = msg.subject ?: "No Subject",
                            sender = sender,
                            body = msg.bodyPreview ?: "",
                            bodyHtml = bodyHtml,
                            dateReceived = dateReceived,
                            syncDate = System.currentTimeMillis()
                        )
                    )
                }
                syncMetadataDao.insertSyncMetadata(SyncMetadataEntity("OUTLOOK", System.currentTimeMillis()))
            } catch (e: Exception) {
                outlookError = e
            }
        }

        // Fallback: If no token was provided, or BOTH failed, we do not generate mock data anymore.
        if (gmailToken.isNullOrBlank() && outlookToken.isNullOrBlank()) {
            return@withContext Result.success(0)
        }

        if (syncedEmails.isNotEmpty()) {
            emailDao.insertEmails(syncedEmails)
        }

        if (gmailError != null && outlookError != null) {
            return@withContext Result.failure(Exception("Gmail: ${gmailError.message}. Outlook: ${outlookError.message}"))
        }

        Result.success(syncedEmails.size)
    }

    override suspend fun syncAll(isBackground: Boolean): Map<String, Result<Int>> {
        val results = mutableMapOf<String, Result<Int>>()
        results["Call Logs"] = syncCallLogs()
        results["Contacts"] = syncContacts()
        results["Calendars"] = syncCalendarsAndEvents()
        results["Emails"] = syncEmails()

        val callLogsCount = results["Call Logs"]?.getOrDefault(0) ?: 0
        val contactsCount = results["Contacts"]?.getOrDefault(0) ?: 0
        val calendarsCount = results["Calendars"]?.getOrDefault(0) ?: 0
        val emailsCount = results["Emails"]?.getOrDefault(0) ?: 0

        val hasError = results.values.any { it.isFailure }
        val status = if (hasError) "PARTIAL" else "SUCCESS"
        val summary = "Synced: $callLogsCount Call Logs, $contactsCount Contacts, $calendarsCount Events, $emailsCount Emails"

        try {
            syncLogDao.insertSyncLog(
                SyncLogEntity(
                    timestamp = System.currentTimeMillis(),
                    isBackground = isBackground,
                    status = status,
                    summary = summary
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }

    override suspend fun captureLocation(): Result<LocationEntity> = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            return@withContext Result.failure(SecurityException("Location permission denied"))
        }

        try {
            // Task-based await for getting last location
            val locationTask = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            val location = Tasks.await(locationTask)
            
            if (location != null) {
                val entity = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = location.time,
                    syncDate = System.currentTimeMillis()
                )
                locationDao.insertLocation(entity)
                syncMetadataDao.insertSyncMetadata(SyncMetadataEntity("LOCATION", System.currentTimeMillis()))
                Result.success(entity)
            } else {
                // Fallback to last known location if current location is unavailable
                val lastKnownTask = fusedLocationClient.lastLocation
                val lastKnown = Tasks.await(lastKnownTask)
                if (lastKnown != null) {
                    val entity = LocationEntity(
                        latitude = lastKnown.latitude,
                        longitude = lastKnown.longitude,
                        timestamp = lastKnown.time,
                        syncDate = System.currentTimeMillis()
                    )
                    locationDao.insertLocation(entity)
                    syncMetadataDao.insertSyncMetadata(SyncMetadataEntity("LOCATION", System.currentTimeMillis()))
                    Result.success(entity)
                } else {
                    Result.failure(Exception("Location unavailable"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractGmailBody(payload: com.abdulaziz733.kinetron.data.network.model.GmailPayload?, preferHtml: Boolean): String {
        if (payload == null) return ""
        
        // 1. Check root payload mimeType directly
        if (payload.mimeType?.contains(if (preferHtml) "text/html" else "text/plain", ignoreCase = true) == true) {
            val data = payload.body?.data
            if (!data.isNullOrBlank()) {
                return decodeBase64Url(data)
            }
        }
        
        // 2. Check parts list
        val parts = payload.parts
        if (parts != null) {
            return findBodyInParts(parts, preferHtml)
        }
        
        // 3. Fallback: if we preferred HTML but got nothing, check text/plain at root
        if (preferHtml) {
            if (payload.mimeType?.contains("text/plain", ignoreCase = true) == true) {
                val data = payload.body?.data
                if (!data.isNullOrBlank()) {
                    return decodeBase64Url(data)
                }
            }
        }
        
        return ""
    }

    private fun findBodyInParts(parts: List<com.abdulaziz733.kinetron.data.network.model.GmailPayloadPart>, preferHtml: Boolean): String {
        val targetMime = if (preferHtml) "text/html" else "text/plain"
        // Try finding target mimeType at this level
        for (part in parts) {
            if (part.mimeType?.contains(targetMime, ignoreCase = true) == true) {
                val data = part.body?.data
                if (!data.isNullOrBlank()) {
                    return decodeBase64Url(data)
                }
            }
        }
        
        // Fallback for html: try finding plain text at this level
        if (preferHtml) {
            for (part in parts) {
                if (part.mimeType?.contains("text/plain", ignoreCase = true) == true) {
                    val data = part.body?.data
                    if (!data.isNullOrBlank()) {
                        return decodeBase64Url(data)
                    }
                }
            }
        }
        
        // Recurse into child parts
        for (part in parts) {
            val subParts = part.parts
            if (subParts != null) {
                val res = findBodyInParts(subParts, preferHtml)
                if (res.isNotEmpty()) {
                    return res
                }
            }
        }
        
        return ""
    }

    private fun decodeBase64Url(data: String): String {
        return try {
            val decodedBytes = android.util.Base64.decode(data, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
            String(decodedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }
}
