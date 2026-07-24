package com.abdulaziz733.kinetron.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.database.entity.ContactEntity
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.utils.DateTimeUtils

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.window.Dialog

/**
 * Organism dialog displaying a contact's synchronized data formatted in vCard, JSON, or CSV, with audit details.
 */
@Composable
fun ContactFormatDetailDialog(
    contact: ContactEntity,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        ContactFormatDetailDialogContent(
            contact = contact,
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
}


/**
 * Component representation of the contact format exporter dialog, optimized for previews.
 */
@Composable
fun ContactFormatDetailDialogContent(
    contact: ContactEntity,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var formatMode by remember { mutableIntStateOf(0) }

    val formattedContent = remember(contact, formatMode) {
        when (formatMode) {
            0 -> """
                BEGIN:VCARD
                VERSION:3.0
                FN:${contact.name}
                TEL;TYPE=CELL:${contact.phone ?: ""}
                EMAIL;TYPE=PREF:${contact.email ?: ""}
                REV:${DateTimeUtils.formatVCardRev(contact.lastUpdatedTimestamp)}
                END:VCARD
            """.trimIndent()

            1 -> """
                {
                  "id": ${contact.id},
                  "name": "${contact.name}",
                  "phone": "${contact.phone ?: ""}",
                  "email": "${contact.email ?: ""}",
                  "deviceLastUpdated": ${contact.lastUpdatedTimestamp},
                  "localSyncTime": ${contact.syncDate}
                }
            """.trimIndent()

            else -> """
                ID,Name,Phone,Email,DeviceTimestamp,SyncTimestamp
                ${contact.id},"${contact.name}","${contact.phone ?: ""}","${contact.email ?: ""}",${contact.lastUpdatedTimestamp},${contact.syncDate}
            """.trimIndent()
        }
    }

    val lastUpdatedText = DateTimeUtils.formatDateTimeSeconds(contact.lastUpdatedTimestamp)
    val syncText = DateTimeUtils.formatDateTimeSeconds(contact.syncDate)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = ToscaOnBg
            )
            Text(
                text = "Specific representation format explorer",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // format selector tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("vCard", "JSON", "CSV").forEachIndexed { index, format ->
                    val isSelected = formatMode == index
                    Button(
                        onClick = { formatMode = index },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) ToscaPrimary else Color(0xFFECEFF1),
                            contentColor = if (isSelected) Color.White else ToscaOnBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = format, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // formatted code container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Box(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = formattedContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF37474F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delta metadata card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SoftOrange)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "ℹ️ Delta Audit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = OnSoftOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• Device Updated: $lastUpdatedText\n• Cache Synced: $syncText",
                        fontSize = 10.sp,
                        color = OnSoftOrange.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = ToscaPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactFormatDetailDialogPreview() {
    MaterialTheme {
        ContactFormatDetailDialogContent(
            contact = ContactEntity(
                id = 1,
                lookupKey = "rian-key",
                name = "Rian CRM Customer",
                phone = "+628765432100",
                email = "rian.customer@email.com",
                lastUpdatedTimestamp = System.currentTimeMillis(),
                syncDate = System.currentTimeMillis()
            ),
            onDismiss = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}


