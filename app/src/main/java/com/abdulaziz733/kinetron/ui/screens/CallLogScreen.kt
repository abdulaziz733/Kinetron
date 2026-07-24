package com.abdulaziz733.kinetron.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronTextField
import com.abdulaziz733.kinetron.ui.components.molecules.CallLogItemRow
import com.abdulaziz733.kinetron.ui.components.molecules.FeatureSyncHeader
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel

/**
 * Screen displaying local Call Logs cached from Device content providers.
 * Adheres to Atomic Design principles using Kinetron components.
 */
@Composable
fun CallLogScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val callLogs by viewModel.callLogs.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(callLogs, searchQuery) {
        callLogs.filter {
            it.number.contains(searchQuery, ignoreCase = true) ||
                    (it.name?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }

    Scaffold(
        topBar = {
            FeatureSyncHeader(
                title = "Call Logs 📞",
                isSyncing = isSyncing,
                onBack = onBack,
                onSync = { viewModel.triggerCallLogsSync() }
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
            // Search Bar Atom
            KinetronTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search by name or number...",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No Call Logs cached. Click the Sync button above! 🔄" else "No matching logs found 🔍",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredLogs) { log ->
                        CallLogItemRow(log = log)
                    }
                }
            }
        }
    }
}
