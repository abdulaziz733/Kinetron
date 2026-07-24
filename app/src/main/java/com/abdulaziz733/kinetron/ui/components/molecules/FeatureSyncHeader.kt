package com.abdulaziz733.kinetron.ui.components.molecules

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary

import androidx.compose.ui.tooling.preview.Preview

/**
 * Reusable screen header molecule containing back navigation, feature title, and manual sync action (with automatic loading state spinner).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureSyncHeader(
    title: String,
    isSyncing: Boolean,
    onBack: () -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).padding(end = 4.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onSync) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync Data")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ToscaPrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun FeatureSyncHeaderStablePreview() {
    MaterialTheme {
        FeatureSyncHeader(
            title = "Call Logs 📞",
            isSyncing = false,
            onBack = {},
            onSync = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureSyncHeaderSyncingPreview() {
    MaterialTheme {
        FeatureSyncHeader(
            title = "Gmail Mailbox 📥",
            isSyncing = true,
            onBack = {},
            onSync = {}
        )
    }
}


