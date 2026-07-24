package com.abdulaziz733.kinetron.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.network.OAuthHelper
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronButton
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel
import com.abdulaziz733.kinetron.utils.DateTimeUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

/**
 * Screen displaying Settings page, Service connection configs, background scheduler status, and sync logs history.
 * Refactored using clean decoupled Atomic Design components.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val isGmailConnected by viewModel.isGmailConnected.collectAsState()
    val isOutlookConnected by viewModel.isOutlookConnected.collectAsState()
    val isSchedulerEnabled by viewModel.isSchedulerEnabled.collectAsState()
    val syncLogs by viewModel.syncLogs.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, OAuthHelper.buildGoogleSignInOptions())
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email
            if (!email.isNullOrBlank()) {
                viewModel.onGmailSignInSuccess(email)
            } else {
                viewModel.showToast("Failed to connect: Could not retrieve Gmail account")
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            viewModel.showToast("Google Sign-In failed: ${e.statusCode}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings & APIs ⚙️", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Services Connection Status ☁️🔌",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ToscaOnBg
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Connect your live accounts to sync emails automatically in the background. If disconnected, Kinetron uses safe mock/simulated data.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    // Gmail Connection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Gmail (Google API)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ToscaOnBg
                            )
                            Text(
                                text = if (isGmailConnected) "Connected ✓" else "Not Connected ✗",
                                fontSize = 12.sp,
                                color = if (isGmailConnected) Color(0xFF2E7D32) else Color.Gray,
                                fontWeight = if (isGmailConnected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        
                        KinetronButton(
                            onClick = {
                                if (isGmailConnected) {
                                    viewModel.disconnectGmail()
                                    googleSignInClient.signOut()
                                } else {
                                    launcher.launch(googleSignInClient.signInIntent)
                                }
                            },
                            containerColor = if (isGmailConnected) Color(0xFFE53935) else ToscaPrimary,
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(if (isGmailConnected) "Disconnect" else "Connect", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color.LightGray)

                    // Outlook Connection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Outlook (MS Graph API)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ToscaOnBg
                            )
                            Text(
                                text = "Coming Soon ⏳",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        KinetronButton(
                            onClick = { },
                            enabled = false,
                            containerColor = Color(0xFFEEEEEE),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text("Connect", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Background Scheduler Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Background Sync Scheduler 🔄📅",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ToscaOnBg
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Auto-sync calls, contacts, events every 20 minutes",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isSchedulerEnabled,
                        onCheckedChange = { viewModel.setSchedulerEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ToscaPrimary,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }

            // Sync Log History Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Event Scheduler Sync Logs 📝",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ToscaOnBg
                        )
                        if (syncLogs.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSyncLogs() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear Logs", tint = Color.Gray)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (syncLogs.isEmpty()) {
                        Text(
                            text = "No logs recorded yet. Trigger a sync or wait for periodic background execution.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            syncLogs.forEach { log ->
                                val dateStr = DateTimeUtils.formatLogDate(log.timestamp)
                                val sourceLabel = if (log.isBackground) "Background ⏰" else "Manual 👤"
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ToscaLightBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = sourceLabel,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ToscaPrimary,
                                                modifier = Modifier
                                                    .background(Color.White, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = dateStr,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = log.summary,
                                            fontSize = 12.sp,
                                            color = ToscaOnBg,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = log.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.status == "SUCCESS") ToscaPrimary else Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
