package com.abdulaziz733.kinetron.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronButton
import com.abdulaziz733.kinetron.ui.components.molecules.LocationItemRow
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel
import com.abdulaziz733.kinetron.utils.DateTimeUtils
import java.util.Locale

/**
 * Screen displaying GPS captured coordinate history and live coordinates display.
 * Designed with clean Atomic Design components.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val locations by viewModel.locations.collectAsState()
    val latestLocation by viewModel.latestLocation.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coordinates Log 📍", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            // Live coordinates display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(18.dp),
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CURRENT COORDINATES 🗺️",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = latestLocation?.let {
                                String.format(Locale.US, "%.6f, %.6f", it.latitude, it.longitude)
                            } ?: "No GPS data cached yet",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latestLocation?.let {
                                "Captured: ${DateTimeUtils.formatDateTimeSeconds(it.timestamp)}"
                            } ?: "Click request below to fetch location",
                            color = ToscaLightBg,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Capture button using styled KinetronButton
            KinetronButton(
                onClick = { viewModel.triggerLocationCapture() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Locate", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capture Current GPS Location! 📍", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }
            }

            Text(
                text = "Location Sync History 🗄️",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ToscaOnBg,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (locations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No location logs stored in Room cache.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(locations) { loc ->
                        LocationItemRow(loc = loc)
                    }
                }
            }
        }
    }
}
