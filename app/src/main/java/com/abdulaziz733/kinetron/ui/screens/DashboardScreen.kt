package com.abdulaziz733.kinetron.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.R
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronButton
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronLoader
import com.abdulaziz733.kinetron.ui.components.molecules.DashboardMenuCard
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main dashboard screen displaying system sync logs, metadata overview, and module grid.
 * Refactored using clean decoupled Atomic Design components.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateTo: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.checkAndHandleFirstLaunchOnboarding {
            onNavigateTo("permissions")
        }
    }

    val callLogs by viewModel.callLogs.collectAsState()

    val contacts by viewModel.contacts.collectAsState()
    val calendars by viewModel.calendars.collectAsState()
    val events by viewModel.events.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val emails by viewModel.emails.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMetadata by viewModel.syncMetadata.collectAsState()

    val lastSyncText = remember(syncMetadata) {
        val latest = syncMetadata.maxOfOrNull { it.lastSyncTimestamp }
        if (latest == null || latest == 0L) {
            "Last Sync: Never"
        } else {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
            "Last Sync: ${sdf.format(Date(latest))}"
        }
    }

    val formattedLocation = remember(locations) {
        val last = locations.firstOrNull()
        if (last == null) {
            "No captures yet"
        } else {
            String.format(Locale.US, "%.4f, %.4f", last.latitude, last.longitude)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kinetron_splash),
                            contentDescription = "Kinetron Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .background(ToscaOnBg, shape = RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Kinetron Engine", fontWeight = FontWeight.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateTo("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ToscaPrimary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ToscaLightBg)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Main Sync Overview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(ToscaPrimary, ToscaSecondary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Kinetron Active Sync",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "LOCAL DATA ✓",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kinetron Engine",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your device data is synced locally, clean & private. Auto-sync is checking in every 15 minutes!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = lastSyncText,
                            color = ToscaTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Sync now button (bouncy looking, Tosca colored)
            KinetronButton(
                onClick = { viewModel.triggerManualSync() },
                enabled = !isSyncing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                if (isSyncing) {
                    KinetronLoader()
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Perform Full Sync Now! 🔄",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Grid cards for different modules (Organism pattern)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DashboardMenuCard(
                        title = "Call Logs",
                        subtitle = "${callLogs.size} logs",
                        icon = Icons.Default.Phone,
                        emoji = "📞",
                        color = Color(0xFFE0F2F1),
                        onClick = { onNavigateTo("call_logs") }
                    )
                }
                item {
                    DashboardMenuCard(
                        title = "Calendars",
                        subtitle = "${calendars.size} calendars\n${events.size} events",
                        icon = Icons.Default.DateRange,
                        emoji = "📅",
                        color = Color(0xFFE8F5E9),
                        onClick = { onNavigateTo("calendar") }
                    )
                }
                item {
                    DashboardMenuCard(
                        title = "Contacts",
                        subtitle = "${contacts.size} contacts",
                        icon = Icons.Default.Person,
                        emoji = "👤",
                        color = Color(0xFFFFF3E0),
                        onClick = { onNavigateTo("contacts") }
                    )
                }
                item {
                    DashboardMenuCard(
                        title = "Emails",
                        subtitle = "${emails.size} fetched",
                        icon = Icons.Default.Email,
                        emoji = "✉️",
                        color = Color(0xFFE3F2FD),
                        onClick = { onNavigateTo("emails") }
                    )
                }
                item {
                    DashboardMenuCard(
                        title = "Location",
                        subtitle = formattedLocation,
                        icon = Icons.Default.LocationOn,
                        emoji = "📍",
                        color = Color(0xFFF3E5F5),
                        onClick = { onNavigateTo("location") }
                    )
                }
                item {
                    DashboardMenuCard(
                        title = "Permissions",
                        subtitle = "Access Keys 🔒",
                        icon = Icons.Default.Lock,
                        emoji = "🛡️",
                        color = Color(0xFFFFEBEE),
                        onClick = { onNavigateTo("permissions") }
                    )
                }
            }
        }
    }
}
