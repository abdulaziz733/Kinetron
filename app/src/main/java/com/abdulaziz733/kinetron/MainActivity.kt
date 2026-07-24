package com.abdulaziz733.kinetron

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.content.Intent
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.abdulaziz733.kinetron.data.sync.SyncScheduler
import com.abdulaziz733.kinetron.ui.screens.*
import com.abdulaziz733.kinetron.ui.theme.KinetronTheme
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import kotlinx.serialization.Serializable

// Define type-safe keys for Navigation 3
@Serializable
sealed interface ScreenKey : NavKey

@Serializable
data object SplashKey : ScreenKey

@Serializable
data object DashboardKey : ScreenKey

@Serializable
data object CallLogsKey : ScreenKey

@Serializable
data object ContactsKey : ScreenKey

@Serializable
data object CalendarKey : ScreenKey

@Serializable
data object EmailsKey : ScreenKey

@Serializable
data object LocationKey : ScreenKey

@Serializable
data object PermissionsKey : ScreenKey

@Serializable
data object SettingsKey : ScreenKey

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule periodic WorkManager sync (deduplicated via unique work name)
        val prefs = getSharedPreferences("kinetron_prefs", MODE_PRIVATE)
        val schedulerEnabled = prefs.getBoolean("scheduler_enabled", true)
        if (schedulerEnabled) {
            SyncScheduler.schedulePeriodicSync(this)
        }

        // Observe sync alerts & Toast messages in lifecycle scope
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.syncResultsMessage.collect { message ->
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        }
        setContent {
            KinetronTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val backStack = rememberNavBackStack(SplashKey)

                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.size - 1)
                            }
                        },
                        entryProvider = entryProvider {
                            entry<SplashKey> {
                                SplashScreen(
                                    onSplashFinished = {
                                        backStack.clear()
                                        backStack.add(DashboardKey)
                                    }
                                )
                            }
                            entry<DashboardKey> {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateTo = { routeName ->
                                        val key = when (routeName) {
                                            "call_logs" -> CallLogsKey
                                            "contacts" -> ContactsKey
                                            "calendar" -> CalendarKey
                                            "emails" -> EmailsKey
                                            "location" -> LocationKey
                                            "permissions" -> PermissionsKey
                                            "settings" -> SettingsKey
                                            else -> DashboardKey
                                        }
                                        if (key != DashboardKey) {
                                            backStack.add(key)
                                        }
                                    }
                                )
                            }
                            entry<CallLogsKey> {
                                CallLogScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            entry<ContactsKey> {
                                ContactScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            entry<CalendarKey> {
                                CalendarScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            entry<EmailsKey> {
                                EmailScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) },
                                    onNavigateToSettings = { backStack.add(SettingsKey) }
                                )
                            }
                            entry<LocationKey> {
                                LocationScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            entry<PermissionsKey> {
                                PermissionScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            entry<SettingsKey> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh permissions status if user changed them in system settings
        viewModel.checkPermissions()
    }
}