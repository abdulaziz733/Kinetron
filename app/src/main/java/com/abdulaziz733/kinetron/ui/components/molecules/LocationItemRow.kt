package com.abdulaziz733.kinetron.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.database.entity.LocationEntity
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.utils.DateTimeUtils
import java.util.Locale

import androidx.compose.ui.tooling.preview.Preview

/**
 * Molecule displaying a single location capture event with latitude, longitude, and timestamps.
 */
@Composable
fun LocationItemRow(
    loc: LocationEntity,
    modifier: Modifier = Modifier
) {
    val capTime = DateTimeUtils.formatDateTimeSeconds(loc.timestamp)
    val syncTime = DateTimeUtils.formatDateTimeSeconds(loc.syncDate)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📌", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = String.format(Locale.US, "%.5f, %.5f", loc.latitude, loc.longitude),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToscaOnBg
                )
                Text(
                    text = "GPS Time: $capTime",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Synced: $syncTime",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationItemRowPreview() {
    LocationItemRow(
        loc = LocationEntity(
            id = 1,
            latitude = -6.200000,
            longitude = 106.816666,
            timestamp = System.currentTimeMillis(),
            syncDate = System.currentTimeMillis()
        )
    )
}

