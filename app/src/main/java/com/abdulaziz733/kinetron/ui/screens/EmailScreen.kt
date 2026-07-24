package com.abdulaziz733.kinetron.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.database.entity.EmailEntity
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronButton
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronTextField
import com.abdulaziz733.kinetron.ui.components.molecules.EmailItemRow
import com.abdulaziz733.kinetron.ui.components.molecules.FeatureSyncHeader
import com.abdulaziz733.kinetron.ui.components.organisms.EmailDetailSheet
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel

/**
 * Screen displaying localized cached email inbox from both Gmail and Outlook providers.
 * Styled following clean architecture and Atomic Design concepts.
 */
@Composable
fun EmailScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val emails by viewModel.emails.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val isGmailConnected by viewModel.isGmailConnected.collectAsState()
    val isOutlookConnected by viewModel.isOutlookConnected.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Gmail, 2: Outlook
    var searchQuery by remember { mutableStateOf("") }
    var selectedEmail by remember { mutableStateOf<EmailEntity?>(null) }

    val filteredEmails = remember(emails, selectedTab, searchQuery) {
        emails.filter {
            when (selectedTab) {
                1 -> it.source == "GMAIL"
                2 -> it.source == "OUTLOOK"
                else -> true
            }
        }.filter {
            it.subject.contains(searchQuery, ignoreCase = true) ||
                    it.sender.contains(searchQuery, ignoreCase = true) ||
                    it.body.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            FeatureSyncHeader(
                title = "Emails ✉️",
                isSyncing = isSyncing,
                onBack = onBack,
                onSync = { viewModel.triggerEmailSync() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ToscaLightBg)
                .padding(innerPadding)
        ) {
            // TabRow
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = ToscaPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ToscaPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All Inbox 📥", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Gmail 🔴", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Outlook 🔵", fontWeight = FontWeight.Bold) }
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Connection Check banner
                val isTargetConnected = when (selectedTab) {
                    1 -> isGmailConnected
                    2 -> isOutlookConnected
                    else -> isGmailConnected || isOutlookConnected
                }

                if (!isTargetConnected) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftRed)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Connection Required! 🚨",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = OnSoftRed
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Your Gmail or Outlook accounts are not connected yet. Connect them in Settings to sync.",
                                    fontSize = 11.sp,
                                    color = OnSoftRed.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            KinetronButton(
                                onClick = onNavigateToSettings,
                                containerColor = OnSoftRed,
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Search Bar Atom
                KinetronTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search by subject, sender or body...",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (filteredEmails.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) {
                                if (isTargetConnected) "Inbox is empty. Click sync above! 🔄"
                                else "Please connect account in Settings first."
                            } else "No matching emails found 🔍",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredEmails) { email ->
                            EmailItemRow(
                                email = email,
                                onClick = { selectedEmail = email }
                            )
                        }
                    }
                }
            }
        }

        // Email Detail Bottom Sheet Organism
        selectedEmail?.let { email ->
            EmailDetailSheet(
                email = email,
                onDismiss = { selectedEmail = null }
            )
        }
    }
}
