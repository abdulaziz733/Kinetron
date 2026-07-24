package com.abdulaziz733.kinetron.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.database.entity.ContactEntity
import com.abdulaziz733.kinetron.ui.theme.OnSoftOrange
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Molecule displaying a single contact row with initials avatar and format view action button.
 */
@Composable
fun ContactItemRow(
    contact: ContactEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.firstOrNull()?.uppercase() ?: "👤",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = OnSoftOrange
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ToscaOnBg
                )
                Text(
                    text = contact.phone ?: "No Phone",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Format View",
                tint = ToscaPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactItemRowPreview() {
    MaterialTheme {
        ContactItemRow(
            contact = ContactEntity(
                id = 1,
                lookupKey = "rian-key",
                name = "Rian CRM Customer",
                phone = "+628765432100",
                email = "rian.customer@email.com",
                lastUpdatedTimestamp = System.currentTimeMillis(),
                syncDate = System.currentTimeMillis()
            ),
            onClick = {}
        )
    }
}

