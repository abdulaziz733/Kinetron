package com.abdulaziz733.kinetron.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.theme.*

import androidx.compose.ui.tooling.preview.Preview

/**
 * Molecule displaying an individual system permission status row with action to request it.
 */
@Composable
fun PermissionItemRow(
    name: String,
    description: String,
    isGranted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isGranted, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToscaOnBg
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isGranted) "Status: Allowed! 🎉" else "Status: Locked 🔒 (Tap to Unlock)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGranted) OnSoftGreen else OnSoftRed
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isGranted) SoftGreen else SoftRed,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = if (isGranted) "Unlocked" else "Locked",
                    tint = if (isGranted) OnSoftGreen else OnSoftRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionItemRowAllowedPreview() {
    PermissionItemRow(
        name = "GPS Location",
        description = "Allows driver and dispatch route synchronization in background.",
        isGranted = true,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PermissionItemRowLockedPreview() {
    PermissionItemRow(
        name = "Call Log Access",
        description = "Read call stats to integrate and capture client communication.",
        isGranted = false,
        onClick = {}
    )
}

