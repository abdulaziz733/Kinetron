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
import com.abdulaziz733.kinetron.data.database.entity.ContactEntity
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronButton
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronTextField
import com.abdulaziz733.kinetron.ui.components.molecules.ContactItemRow
import com.abdulaziz733.kinetron.ui.components.molecules.FeatureSyncHeader
import com.abdulaziz733.kinetron.ui.components.organisms.ContactFormatDetailDialog
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel
import com.abdulaziz733.kinetron.utils.DateTimeUtils

/**
 * Screen displaying contact list synced locally and form to add new contacts to the system provider.
 * Follows Atomic Design structure with clean decoupled components.
 */
@Composable
fun ContactScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMetadata by viewModel.syncMetadata.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedContact by remember { mutableStateOf<ContactEntity?>(null) }

    // Forms
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    var newContactEmail by remember { mutableStateOf("") }

    val filteredContacts = remember(contacts, searchQuery) {
        contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    (it.phone?.contains(searchQuery, ignoreCase = true) ?: false) ||
                    (it.email?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }

    val contactSyncMetadata = syncMetadata.find { it.syncType == "CONTACT" }
    val lastSyncText = contactSyncMetadata?.let {
        DateTimeUtils.formatDateTimeSeconds(it.lastSyncTimestamp)
    } ?: "Never"

    Scaffold(
        topBar = {
            FeatureSyncHeader(
                title = "Contacts 👤",
                isSyncing = isSyncing,
                onBack = onBack,
                onSync = { viewModel.triggerContactsSync() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ToscaLightBg)
                .padding(innerPadding)
        ) {
            // Tabs
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
                    text = { Text("Contact List 📋", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Create New Contact ➕", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                // List Tab
                Column(modifier = Modifier.padding(16.dp)) {
                    // Delta sync information card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftBlue)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "⏱️ Delta Sync Engine Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = OnSoftBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Last successful sync: $lastSyncText.\nNext sync pulls only changes since this timestamp to preserve network & battery.",
                                fontSize = 11.sp,
                                color = OnSoftBlue.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Search Bar Atom
                    KinetronTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search by name, phone or email...",
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (filteredContacts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) "No Contacts cached. Click Sync above! 🔄" else "No matching contacts found 🔍",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredContacts) { contact ->
                                ContactItemRow(
                                    contact = contact,
                                    onClick = { selectedContact = contact }
                                )
                            }
                        }
                    }
                }
            } else {
                // Form Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Playful Custom Contact ✍️🤩",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ToscaOnBg
                    )

                    KinetronTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        placeholder = "Full Name"
                    )

                    KinetronTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        placeholder = "Phone Number"
                    )

                    KinetronTextField(
                        value = newContactEmail,
                        onValueChange = { newContactEmail = it },
                        placeholder = "Email Address"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    KinetronButton(
                        onClick = {
                            if (newContactName.isNotBlank()) {
                                viewModel.createContact(newContactName, newContactPhone, newContactEmail)
                                newContactName = ""
                                newContactPhone = ""
                                newContactEmail = ""
                                selectedTab = 0
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Contact to Device & Sync! 🚀", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Contact format detail dialog
        selectedContact?.let { contact ->
            ContactFormatDetailDialog(
                contact = contact,
                onDismiss = { selectedContact = null }
            )
        }
    }
}
