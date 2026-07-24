package com.abdulaziz733.kinetron.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdulaziz733.kinetron.data.database.entity.*
import com.abdulaziz733.kinetron.data.repository.DeviceDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DeviceDataRepository
) : ViewModel() {

    val callLogs: StateFlow<List<CallLogEntity>> = repository.getCallLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<ContactEntity>> = repository.getContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendars: StateFlow<List<CalendarEntity>> = repository.getCalendars()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<EventEntity>> = repository.getEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emails: StateFlow<List<EmailEntity>> = repository.getEmails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locations: StateFlow<List<LocationEntity>> = repository.getLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestLocation: StateFlow<LocationEntity?> = repository.getLatestLocation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val syncMetadata: StateFlow<List<SyncMetadataEntity>> = repository.getSyncMetadata()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _permissionStatuses = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val permissionStatuses: StateFlow<Map<String, Boolean>> = _permissionStatuses.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncResultsMessage = MutableSharedFlow<String>()
    val syncResultsMessage: SharedFlow<String> = _syncResultsMessage.asSharedFlow()

    private val _outlookToken = MutableStateFlow("")
    val outlookToken: StateFlow<String> = _outlookToken.asStateFlow()


    val syncLogs: StateFlow<List<SyncLogEntity>> = repository.getSyncLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSchedulerEnabled = MutableStateFlow(true)
    val isSchedulerEnabled: StateFlow<Boolean> = _isSchedulerEnabled.asStateFlow()

    init {
        checkPermissions()
        _outlookToken.value = repository.getOutlookToken() ?: ""
        _isSchedulerEnabled.value = repository.isSchedulerEnabled()
    }

    fun setSchedulerEnabled(enabled: Boolean) {
        _isSchedulerEnabled.value = enabled
        repository.setSchedulerEnabled(enabled)
    }

    fun clearSyncLogs() {
        viewModelScope.launch {
            repository.clearSyncLogs()
        }
    }

    fun checkPermissions() {
        _permissionStatuses.value = repository.getPermissionStatuses()
    }

    fun checkAndHandleFirstLaunchOnboarding(onNavigateToPermissions: () -> Unit) {
        if (repository.isFirstLaunchOnboarding()) {
            repository.setFirstLaunchOnboardingCompleted()
            onNavigateToPermissions()
        }
    }

    private val _isGmailConnected = MutableStateFlow(!repository.getGmailAccountEmail().isNullOrBlank())
    val isGmailConnected: StateFlow<Boolean> = _isGmailConnected.asStateFlow()

    private val _isOutlookConnected = MutableStateFlow(!repository.getOutlookToken().isNullOrBlank())
    val isOutlookConnected: StateFlow<Boolean> = _isOutlookConnected.asStateFlow()

    /**
     * Called after successful Google Sign-In.
     * Saves account email as the connected state marker.
     * Token retrieval and refresh are handled by Play Services automatically.
     */
    fun onGmailSignInSuccess(accountEmail: String) {
        repository.saveGmailAccountEmail(accountEmail)
        _isGmailConnected.value = true
        viewModelScope.launch {
            _syncResultsMessage.emit("✓ Gmail Connected: $accountEmail 🚀")
            triggerEmailSync()
        }
    }

    fun disconnectGmail() {
        repository.saveGmailAccountEmail(null)
        _isGmailConnected.value = false
        viewModelScope.launch {
            repository.deleteEmailsBySource("GMAIL")
            repository.deleteSyncMetadataByType("GMAIL")
            _syncResultsMessage.emit("Gmail disconnected successfully")
        }
    }

    fun disconnectOutlook() {
        repository.saveOutlookToken(null)
        _isOutlookConnected.value = false
        viewModelScope.launch {
            repository.deleteEmailsBySource("OUTLOOK")
            repository.deleteSyncMetadataByType("OUTLOOK")
            _syncResultsMessage.emit("Outlook disconnected successfully")
        }
    }

    fun saveOutlookToken(token: String) {
        _outlookToken.value = token
        repository.saveOutlookToken(token.ifBlank { null })
    }

    /**
     * Silent foreground sync triggered on every app open.
     * Bypasses Samsung/OEM battery optimizations that kill WorkManager.
     * Runs quietly without showing loading spinner or toast.
     */
    fun showToast(message: String) {
        viewModelScope.launch {
            _syncResultsMessage.emit(message)
        }
    }

    fun triggerStartupSync() {
        viewModelScope.launch {
            try {
                repository.syncAll(isBackground = false)
                repository.captureLocation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            checkPermissions()
        }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val results = repository.syncAll()
            _isSyncing.value = false
            
            val successMsg = results.entries.joinToString("\n") { (key, result) ->
                if (result.isSuccess) {
                    "✓ $key: Synced ${result.getOrNull() ?: 0} items"
                } else {
                    "✗ $key: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
                }
            }
            _syncResultsMessage.emit(successMsg)
            checkPermissions()
        }
    }

    fun triggerCallLogsSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncCallLogs()
            _isSyncing.value = false
            if (result.isSuccess) {
                _syncResultsMessage.emit("✓ Call Logs: Synced ${result.getOrNull()} items")
            } else {
                _syncResultsMessage.emit("✗ Call Logs: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun triggerContactsSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncContacts()
            _isSyncing.value = false
            if (result.isSuccess) {
                _syncResultsMessage.emit("✓ Contacts: Synced ${result.getOrNull()} items")
            } else {
                _syncResultsMessage.emit("✗ Contacts: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun triggerCalendarSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncCalendarsAndEvents()
            _isSyncing.value = false
            if (result.isSuccess) {
                _syncResultsMessage.emit("✓ Calendars & Events: Synced ${result.getOrNull()} items")
            } else {
                _syncResultsMessage.emit("✗ Calendars & Events: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun triggerEmailSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncEmails()
            _isSyncing.value = false
            if (result.isSuccess) {
                _syncResultsMessage.emit("✓ Emails: Synced ${result.getOrNull()} items")
            } else {
                _syncResultsMessage.emit("✗ Emails: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun triggerLocationCapture() {
        viewModelScope.launch {
            val result = repository.captureLocation()
            if (result.isSuccess) {
                val loc = result.getOrNull()
                _syncResultsMessage.emit("✓ Location Captured: Lat ${loc?.latitude}, Long ${loc?.longitude}")
            } else {
                _syncResultsMessage.emit("✗ Location: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun createCalendar(name: String) {
        viewModelScope.launch {
            val success = repository.createLocalCalendar(name)
            if (success) {
                _syncResultsMessage.emit("✓ Calendar '$name' created locally")
            } else {
                _syncResultsMessage.emit("✗ Failed to create Calendar '$name'")
            }
        }
    }

    fun createContact(name: String, phone: String, email: String) {
        viewModelScope.launch {
            val success = repository.createContact(name, phone, email)
            if (success) {
                _syncResultsMessage.emit("✓ Contact '$name' created locally")
            } else {
                _syncResultsMessage.emit("✗ Failed to create Contact '$name'")
            }
        }
    }

    fun createEvent(calendarId: Long, title: String, description: String?, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            val success = repository.createEvent(calendarId, title, description, startTime, endTime)
            if (success) {
                _syncResultsMessage.emit("✓ Event '$title' created successfully")
            } else {
                _syncResultsMessage.emit("✗ Failed to create Event '$title'")
            }
        }
    }
}
