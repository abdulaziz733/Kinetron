package com.abdulaziz733.kinetron.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.database.entity.EmailEntity
import com.abdulaziz733.kinetron.ui.components.atoms.HtmlEmailViewer
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.utils.DateTimeUtils
import kotlin.math.abs

import androidx.compose.ui.tooling.preview.Preview

/**
 * Organism representing the details of an email. Opens inside a ModalBottomSheet and parses HTML dynamically.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailDetailSheet(
    email: EmailEntity,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = DateTimeUtils.formatEmailDetailDate(email.dateReceived)
    val syncText = DateTimeUtils.formatDateTimeSeconds(email.syncDate)

    // Derive avatar character and color
    val senderName = email.sender.substringBefore("<").trim()
    val initial = senderName.firstOrNull()?.toString()?.uppercase() ?: "?"
    val avatarBgColor = remember(email.sender) {
        val hash = email.sender.hashCode()
        val colors = listOf(
            Color(0xFFE53935), Color(0xFFD81B60), Color(0xFF8E24AA), Color(0xFF5E35B1),
            Color(0xFF3949AB), Color(0xFF1E88E5), Color(0xFF00ACC1), Color(0xFF00897B),
            Color(0xFF43A047), Color(0xFF7CB342), Color(0xFFF4511E), Color(0xFF795548)
        )
        colors[abs(hash) % colors.size]
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        dragHandle = null,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ToscaOnBg
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { /* Archive placeholder */ }) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive",
                            tint = ToscaOnBg
                        )
                    }
                    IconButton(onClick = { /* Delete placeholder */ }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ToscaOnBg
                        )
                    }
                    IconButton(onClick = { /* Mark as Unread placeholder */ }) {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = "Mark unread",
                            tint = ToscaOnBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable email content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Email Subject
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ToscaOnBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Sender Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Circle Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(color = avatarBgColor, shape = RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Sender name and email details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = senderName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ToscaOnBg
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "to me",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Details",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFECEFF1))
                Spacer(modifier = Modifier.height(24.dp))

                // Email Body (HTML WebView or Text fallback)
                if (!email.bodyHtml.isNullOrBlank()) {
                    HtmlEmailViewer(
                        htmlContent = email.bodyHtml,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                } else {
                    Text(
                        text = email.body,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFF263238)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFECEFF1))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Local Sync Timestamp: $syncText",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailDetailSheetPreview() {
    // Basic inner layout preview without showing ModalBottomSheet overlay container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "CRM Leads Centralized Update",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = ToscaOnBg
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = ToscaPrimary, shape = RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Sales Operations Team", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ToscaOnBg)
                Text(text = "to me", fontSize = 12.sp, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFECEFF1))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This is a dummy email preview representation for testing UI layout inside Android Studio.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

