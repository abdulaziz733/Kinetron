package com.abdulaziz733.kinetron.ui.components.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.data.database.entity.CalendarEntity
import com.abdulaziz733.kinetron.ui.components.atoms.KinetronTextField
import com.abdulaziz733.kinetron.ui.theme.OnSoftRed
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary
import java.util.Calendar

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment

/**
 * Organism dialog displaying fields to add an event to the selected local calendar.
 */
@Composable
fun EventAddDialog(
    selectedDayLabel: String,
    selectedCal: Calendar,
    calendars: List<CalendarEntity>,
    onCreateEvent: (calendarId: Long, title: String, description: String?, startTime: Long, endTime: Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        EventAddDialogContent(
            selectedDayLabel = selectedDayLabel,
            selectedCal = selectedCal,
            calendars = calendars,
            onCreateEventClick = { calId, title, desc, start, end ->
                onCreateEvent(calId, title, desc, start, end)
                onDismiss()
            },
            onCancelClick = onDismiss,
            modifier = modifier
        )
    }
}

/**
 * Component representation of the add event form, optimized for previews.
 */
@Composable
fun EventAddDialogContent(
    selectedDayLabel: String,
    selectedCal: Calendar,
    calendars: List<CalendarEntity>,
    onCreateEventClick: (calendarId: Long, title: String, description: String?, startTime: Long, endTime: Long) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newEventTitle by remember { mutableStateOf("") }
    var newEventDesc by remember { mutableStateOf("") }
    var selectedCalendarId by remember { mutableStateOf<Long?>(null) }

    val initDay = remember(selectedCal) { selectedCal.get(Calendar.DAY_OF_MONTH).toString() }
    val initMonth = remember(selectedCal) { (selectedCal.get(Calendar.MONTH) + 1).toString() }
    val initYear = remember(selectedCal) { selectedCal.get(Calendar.YEAR).toString() }

    var dayInput by remember { mutableStateOf(initDay) }
    var monthInput by remember { mutableStateOf(initMonth) }
    var yearInput by remember { mutableStateOf(initYear) }

    var startHourInput by remember { mutableStateOf("09") }
    var startMinuteInput by remember { mutableStateOf("00") }

    var endHourInput by remember { mutableStateOf("10") }
    var endMinuteInput by remember { mutableStateOf("00") }

    val localCalendars = remember(calendars) {
        calendars.filter { cal ->
            cal.accountType.equals("LOCAL", ignoreCase = true)
        }
    }

    LaunchedEffect(localCalendars) {
        if (localCalendars.isNotEmpty() && (selectedCalendarId == null || localCalendars.none { it.id == selectedCalendarId })) {
            selectedCalendarId = localCalendars.first().id
        }
    }

    val selectedLocalCalendar = remember(localCalendars, selectedCalendarId) {
        localCalendars.find { it.id == selectedCalendarId } ?: localCalendars.firstOrNull()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Add Event to Calendar",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ToscaOnBg
            )
            Text(
                text = "Schedule custom system task reminder",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                KinetronTextField(
                    value = newEventTitle,
                    onValueChange = { newEventTitle = it },
                    placeholder = "Event Title (e.g. Call Client)"
                )
                KinetronTextField(
                    value = newEventDesc,
                    onValueChange = { newEventDesc = it },
                    placeholder = "Description (Optional)"
                )
                
                // Date Pickers Input Row
                Text(
                    text = "Scheduled Date:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dayInput,
                        onValueChange = { if (it.length <= 2) dayInput = it },
                        label = { Text("Day") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = monthInput,
                        onValueChange = { if (it.length <= 2) monthInput = it },
                        label = { Text("Month") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = yearInput,
                        onValueChange = { if (it.length <= 4) yearInput = it },
                        label = { Text("Year") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Time Pickers Input Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Time:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = startHourInput,
                                onValueChange = { if (it.length <= 2) startHourInput = it },
                                label = { Text("HH") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = startMinuteInput,
                                onValueChange = { if (it.length <= 2) startMinuteInput = it },
                                label = { Text("MM") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End Time:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = endHourInput,
                                onValueChange = { if (it.length <= 2) endHourInput = it },
                                label = { Text("HH") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = endMinuteInput,
                                onValueChange = { if (it.length <= 2) endMinuteInput = it },
                                label = { Text("MM") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }
                
                Text(
                    text = "Choose Calendar Container:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                if (localCalendars.isEmpty()) {
                    Text(
                        text = "No local calendars found. Please create a local calendar container first using the folder icon in calendar bar.",
                        color = OnSoftRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    var dropdownExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedLocalCalendar?.name ?: "Select Calendar Container",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ToscaOnBg,
                                unfocusedTextColor = ToscaOnBg,
                                focusedBorderColor = ToscaPrimary,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown indicator",
                                    tint = ToscaPrimary
                                )
                            }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { dropdownExpanded = true }
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        localCalendars.forEach { cal ->
                            DropdownMenuItem(
                                text = { Text(cal.name) },
                                onClick = {
                                    selectedCalendarId = cal.id
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancelClick) {
                    Text("Cancel", color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val calId = selectedCalendarId
                        if (newEventTitle.isNotBlank() && calId != null) {
                            val day = dayInput.toIntOrNull() ?: selectedCal.get(Calendar.DAY_OF_MONTH)
                            val month = (monthInput.toIntOrNull() ?: (selectedCal.get(Calendar.MONTH) + 1)) - 1
                            val year = yearInput.toIntOrNull() ?: selectedCal.get(Calendar.YEAR)

                            val sHour = startHourInput.toIntOrNull() ?: 9
                            val sMin = startMinuteInput.toIntOrNull() ?: 0
                            val eHour = endHourInput.toIntOrNull() ?: 10
                            val eMin = endMinuteInput.toIntOrNull() ?: 0

                            val startCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, day)
                                set(Calendar.HOUR_OF_DAY, sHour)
                                set(Calendar.MINUTE, sMin)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            val endCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, day)
                                set(Calendar.HOUR_OF_DAY, eHour)
                                set(Calendar.MINUTE, eMin)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            
                            onCreateEventClick(calId, newEventTitle, newEventDesc.ifBlank { null }, startCal.timeInMillis, endCal.timeInMillis)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToscaPrimary)
                ) {
                    Text("Add Event")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventAddDialogPreview() {
    MaterialTheme {
        EventAddDialogContent(
            selectedDayLabel = "24 July 2026",
            selectedCal = Calendar.getInstance(),
            calendars = listOf(
                CalendarEntity(1, "Main Local Calendar", "local_user", "LOCAL", System.currentTimeMillis()),
                CalendarEntity(2, "Company Events", "local_corp", "LOCAL", System.currentTimeMillis())
            ),
            onCreateEventClick = { _, _, _, _, _ -> },
            onCancelClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}


