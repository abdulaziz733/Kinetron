package com.abdulaziz733.kinetron.ui.components.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronTextField
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.window.Dialog

/**
 * Organism dialog displaying fields to create a local calendar container.
 */
@Composable
fun CalendarContainerDialog(
    onCreateCalendar: (name: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newCalName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        CalendarContainerDialogContent(
            newCalName = newCalName,
            onCalNameChange = { newCalName = it },
            onCreateClick = {
                if (newCalName.isNotBlank()) {
                    onCreateCalendar(newCalName)
                    onDismiss()
                }
            },
            onCancelClick = onDismiss,
            modifier = modifier
        )
    }
}


/**
 * Component representation of the calendar creation form, optimized for previews.
 */
@Composable
fun CalendarContainerDialogContent(
    newCalName: String,
    onCalNameChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Create Calendar Container 📅",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ToscaOnBg
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Create a calendar category on device before adding events.",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            KinetronTextField(
                value = newCalName,
                onValueChange = onCalNameChange,
                placeholder = "Calendar Name"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancelClick) {
                    Text("Cancel", color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onCreateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ToscaPrimary)
                ) {
                    Text("Create")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarContainerDialogPreview() {
    MaterialTheme {
        CalendarContainerDialogContent(
            newCalName = "Driver Shifts Calendar",
            onCalNameChange = {},
            onCreateClick = {},
            onCancelClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}


