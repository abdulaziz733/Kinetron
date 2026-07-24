package com.abdulaziz733.kinetron.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.abdulaziz733.kinetron.data.database.entity.EmailEntity
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.utils.DateTimeUtils

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Molecule displaying a single email list item row.
 */
@Composable
fun EmailItemRow(
    email: EmailEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = DateTimeUtils.formatEmailDate(email.dateReceived)
    val isGmail = email.source == "GMAIL"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            // Source icon box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (isGmail) Color(0xFFFFEBEE) else Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGmail) "🔴" else "🔵",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = email.sender.substringBefore(" <"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ToscaOnBg,
                        maxLines = 1
                    )
                    Text(
                        text = dateString,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = email.subject,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = ToscaOnBg,
                    maxLines = 1
                )
                Text(
                    text = email.body,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailItemRowPreview() {
    MaterialTheme {
        EmailItemRow(
            email = EmailEntity(
                id = "test-gmail-id",
                source = "GMAIL",
                sender = "Sales Leads <leads@google.com>",
                subject = "New Inbound Inquiry for CRM Integration",
                body = "Hello Kinetron Team, we are interested in centralizing client communications...",
                bodyHtml = "<h3>Inbound Inquiry</h3>",
                dateReceived = System.currentTimeMillis(),
                syncDate = System.currentTimeMillis()
            ),
            onClick = {}
        )
    }
}

