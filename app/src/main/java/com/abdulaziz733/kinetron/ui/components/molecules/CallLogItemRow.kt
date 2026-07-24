package com.abdulaziz733.kinetron.ui.components.molecules

import android.provider.CallLog
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
import com.abdulaziz733.kinetron.data.database.entity.CallLogEntity
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.utils.DateTimeUtils

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Molecule displaying a single Call Log entry with formatted duration, type indicators, and timestamp.
 */
@Composable
fun CallLogItemRow(
    log: CallLogEntity,
    modifier: Modifier = Modifier
) {
    val dateString = DateTimeUtils.formatDateTime(log.date)
    val syncText = DateTimeUtils.formatTimeOnly(log.syncDate)

    val (typeLabel, typeColor, typeEmoji) = when (log.type) {
        CallLog.Calls.INCOMING_TYPE -> Triple("Incoming", OnSoftGreen, "📥")
        CallLog.Calls.OUTGOING_TYPE -> Triple("Outgoing", OnSoftBlue, "📤")
        CallLog.Calls.MISSED_TYPE -> Triple("Missed", OnSoftRed, "❌")
        CallLog.Calls.VOICEMAIL_TYPE -> Triple("Voicemail", OnSoftOrange, "📼")
        CallLog.Calls.REJECTED_TYPE -> Triple("Rejected", OnSoftRed, "🚫")
        CallLog.Calls.BLOCKED_TYPE -> Triple("Blocked", Color.DarkGray, "🔕")
        else -> Triple("Unknown", Color.Gray, "📞")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
                    .size(44.dp)
                    .background(
                        color = when (log.type) {
                            CallLog.Calls.INCOMING_TYPE -> SoftGreen
                            CallLog.Calls.OUTGOING_TYPE -> SoftBlue
                            CallLog.Calls.MISSED_TYPE -> SoftRed
                            CallLog.Calls.VOICEMAIL_TYPE -> SoftOrange
                            CallLog.Calls.REJECTED_TYPE -> SoftRed
                            else -> Color(0xFFECEFF1)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = typeEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.name ?: log.number,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToscaOnBg
                )
                if (log.name != null) {
                    Text(
                        text = log.number,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = typeLabel,
                        color = typeColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = " • $dateString",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = DateTimeUtils.formatDuration(log.duration),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = ToscaOnBg
                )
                Text(
                    text = "Synced: $syncText",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CallLogItemRowPreview() {
    MaterialTheme {
        CallLogItemRow(
            log = CallLogEntity(
                id = 1,
                number = "+628123456789",
                name = "Ahmad Driver (Expedisi)",
                type = 1, // CallLog.Calls.INCOMING_TYPE
                date = System.currentTimeMillis(),
                duration = 120,
                syncDate = System.currentTimeMillis()
            )
        )
    }
}

