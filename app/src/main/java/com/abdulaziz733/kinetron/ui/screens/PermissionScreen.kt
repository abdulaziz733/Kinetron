package com.abdulaziz733.kinetron.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronButton
import com.abdulaziz733.kinetron.ui.components.molecules.PermissionItemRow
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel

/**
 * Screen displaying system permissions status and providing custom requests.
 * Uses atomic components to modularize and decouple view implementations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val permissionStatuses by viewModel.permissionStatuses.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.checkPermissions()
    }

    val singlePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { result ->
        viewModel.checkPermissions()
    }

    val permissionsToRequest = mutableListOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    val isForegroundLocationGranted = permissionStatuses["Location (Fine)"] == true || permissionStatuses["Location (Coarse)"] == true
    val isBackgroundLocationGranted = permissionStatuses.getOrDefault("Location (Background)", true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Access Keys 🔑", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = ToscaPrimary,
                     titleContentColor = Color.White,
                     navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ToscaLightBg)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Intro Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Unlocking Permissions 🔓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ToscaOnBg
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kinetron requests permission access to mirror logs locally in your Room database. Tap any locked card to request permission individually, or tap the button at the bottom.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // Lock list of molecules
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(permissionStatuses.entries.toList()) { entry ->
                    val permissionKey = entry.key
                    val isGranted = entry.value
                    
                    PermissionItemRow(
                        name = permissionKey,
                        description = getPermissionDescription(permissionKey),
                        isGranted = isGranted,
                        onClick = {
                            if (!isGranted) {
                                val sysPermission = mapNameToPermission(permissionKey)
                                if (sysPermission != null) {
                                    singlePermissionLauncher.launch(sysPermission)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unlock button
            KinetronButton(
                onClick = { permissionLauncher.launch(permissionsToRequest) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Request All Access Keys 🗝️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            if (isForegroundLocationGranted && !isBackgroundLocationGranted) {
                Spacer(modifier = Modifier.height(8.dp))
                KinetronButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            singlePermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFFE0A96D)
                ) {
                    Text(
                        text = "Allow Background Location 📍",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: Please select 'Allow all the time' (Izinkan sepanjang waktu) in the system page.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getPermissionDescription(name: String): String {
    return when (name) {
        "Call Log" -> "Reads call logs to cache history locally."
        "Calendar (Read)" -> "Accesses system calendar events and schedule details."
        "Calendar (Write)" -> "Allows creating and managing local calendar events."
        "Contact (Read)" -> "Allows reading contact details to mirror locally."
        "Contact (Write)" -> "Allows creating and editing contact entries."
        "Location (Fine)" -> "Captures precise GPS coordinates when requested."
        "Location (Coarse)" -> "Retrieves approximate location coordinates."
        "Location (Background)" -> "Captures coordinates dynamically when app is closed."
        "Notifications" -> "Required to show status alerts on sync operations."
        else -> "System permission key required by Kinetron."
    }
}

private fun mapNameToPermission(name: String): String? {
    return when (name) {
        "Call Log" -> Manifest.permission.READ_CALL_LOG
        "Calendar (Read)" -> Manifest.permission.READ_CALENDAR
        "Calendar (Write)" -> Manifest.permission.WRITE_CALENDAR
        "Contact (Read)" -> Manifest.permission.READ_CONTACTS
        "Contact (Write)" -> Manifest.permission.WRITE_CONTACTS
        "Location (Fine)" -> Manifest.permission.ACCESS_FINE_LOCATION
        "Location (Coarse)" -> Manifest.permission.ACCESS_COARSE_LOCATION
        "Location (Background)" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else null
        "Notifications" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else null
        else -> null
    }
}
