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
import com.abdulaziz733.kinetron.data.database.entity.EventEntity
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary
import com.abdulaziz733.kinetron.utils.DateTimeUtils

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme

/**
 * Molecule displaying a single Calendar Event list item row.
 */
@Composable
fun EventItemRow(
    event: EventEntity,
    modifier: Modifier = Modifier
) {
    val startTimeText = DateTimeUtils.formatTimeNoSeconds(event.startTime)
    val endTimeText = DateTimeUtils.formatTimeNoSeconds(event.endTime)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Event label line indicator (purple/green style)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(
                        color = if (event.calendarId % 2 == 0L) ToscaPrimary else Color(0xFF9C27B0),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ToscaOnBg
                )
                if (!event.description.isNullOrBlank()) {
                    Text(
                        text = event.description,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Text(
                    text = "$startTimeText - $endTimeText",
                    fontSize = 10.sp,
                    color = ToscaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventItemRowPreview() {
    MaterialTheme {
        EventItemRow(
            event = EventEntity(
                id = 1,
                calendarId = 1,
                title = "Driver Dispatch Scheduling",
                description = "Synchronize vehicle route checklist with Ahmad Driver.",
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 3600000,
                syncDate = System.currentTimeMillis()
            )
        )
    }
}

