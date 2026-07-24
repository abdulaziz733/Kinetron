package com.abdulaziz733.kinetron.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.components.molecules.CalendarCell
import com.abdulaziz733.kinetron.ui.components.molecules.EventItemRow
import com.abdulaziz733.kinetron.ui.components.molecules.FeatureSyncHeader
import com.abdulaziz733.kinetron.ui.components.organisms.CalendarContainerDialog
import com.abdulaziz733.kinetron.ui.components.organisms.EventAddDialog
import com.abdulaziz733.kinetron.ui.theme.*
import com.abdulaziz733.kinetron.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen displaying localized cached calendar events, categories, and month grid navigator.
 * Follows Atomic Design structure with clean decoupled modules.
 */
@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val calendars by viewModel.calendars.collectAsState()
    val events by viewModel.events.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    var viewedCal by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedCal by remember { mutableStateOf(Calendar.getInstance()) }

    var showAddEventDialog by remember { mutableStateOf(false) }
    var showCreateCalendarDialog by remember { mutableStateOf(false) }

    val displayedMonthName = remember(viewedCal) {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.US)
        sdf.format(viewedCal.time).uppercase()
    }

    val daysGrid = remember(viewedCal) {
        val grid = mutableListOf<CalendarDay>()
        
        val temp = viewedCal.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK)
        val shift = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

        if (shift > 0) {
            val prevMonth = temp.clone() as Calendar
            prevMonth.add(Calendar.MONTH, -1)
            val maxPrevDays = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in (maxPrevDays - shift + 1)..maxPrevDays) {
                grid.add(
                    CalendarDay(
                        day = i,
                        month = prevMonth.get(Calendar.MONTH),
                        year = prevMonth.get(Calendar.YEAR),
                        isCurrentMonth = false
                    )
                )
            }
        }

        val maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDays) {
            grid.add(
                CalendarDay(
                    day = i,
                    month = temp.get(Calendar.MONTH),
                    year = temp.get(Calendar.YEAR),
                    isCurrentMonth = true
                )
            )
        }

        val totalCells = 42
        val remaining = totalCells - grid.size
        if (remaining > 0) {
            val nextMonth = temp.clone() as Calendar
            nextMonth.add(Calendar.MONTH, 1)
            for (i in 1..remaining) {
                grid.add(
                    CalendarDay(
                        day = i,
                        month = nextMonth.get(Calendar.MONTH),
                        year = nextMonth.get(Calendar.YEAR),
                        isCurrentMonth = false
                    )
                )
            }
        }
        grid
    }

    val selectedDayLabel = remember(selectedCal) {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        sdf.format(selectedCal.time)
    }

    val selectedDayEvents = remember(events, selectedCal) {
        events.filter { event ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.startTime }
            eventCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH) &&
                    eventCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    eventCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)
        }
    }

    Scaffold(
        topBar = {
            FeatureSyncHeader(
                title = "Calendar 📅",
                isSyncing = isSyncing,
                onBack = onBack,
                onSync = { viewModel.triggerCalendarSync() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ToscaLightBg)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Month grid navigation card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val next = viewedCal.clone() as Calendar
                            next.add(Calendar.MONTH, -1)
                            viewedCal = next
                        }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = ToscaPrimary)
                        }
                        Text(
                            text = displayedMonthName,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = ToscaOnBg,
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = {
                            val next = viewedCal.clone() as Calendar
                            next.add(Calendar.MONTH, 1)
                            viewedCal = next
                        }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = ToscaPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days of week header labels
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Grid days cells
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val rows = daysGrid.chunked(7)
                        rows.forEach { rowDays ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowDays.forEach { cellDay ->
                                    val isSelected = selectedCal.get(Calendar.DAY_OF_MONTH) == cellDay.day &&
                                            selectedCal.get(Calendar.MONTH) == cellDay.month &&
                                            selectedCal.get(Calendar.YEAR) == cellDay.year

                                    val today = Calendar.getInstance()
                                    val isToday = today.get(Calendar.DAY_OF_MONTH) == cellDay.day &&
                                            today.get(Calendar.MONTH) == cellDay.month &&
                                            today.get(Calendar.YEAR) == cellDay.year

                                    val hasEvent = events.any { ev ->
                                        val evCal = Calendar.getInstance().apply { timeInMillis = ev.startTime }
                                        evCal.get(Calendar.DAY_OF_MONTH) == cellDay.day &&
                                                evCal.get(Calendar.MONTH) == cellDay.month &&
                                                evCal.get(Calendar.YEAR) == cellDay.year
                                    }

                                    CalendarCell(
                                        cell = cellDay,
                                        isSelected = isSelected,
                                        isToday = isToday,
                                        hasEvent = hasEvent,
                                        onClick = {
                                            val nextSelect = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, cellDay.year)
                                                set(Calendar.MONTH, cellDay.month)
                                                set(Calendar.DAY_OF_MONTH, cellDay.day)
                                            }
                                            selectedCal = nextSelect
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Events List Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Events on $selectedDayLabel 🗓️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ToscaOnBg
                    )
                    Text(
                        text = "Viewing visible container categories",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Row {
                    IconButton(onClick = { showCreateCalendarDialog = true }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "Add Calendar Container", tint = ToscaPrimary)
                    }
                    IconButton(onClick = { showAddEventDialog = true }) {
                        Icon(Icons.Default.AddBox, contentDescription = "Add Event to Calendar", tint = ToscaPrimary)
                    }
                }
            }

            // Events list
            if (selectedDayEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No events scheduled for this day 💤",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(selectedDayEvents) { event ->
                        EventItemRow(event = event)
                    }
                }
            }
        }

        // Dialogs Organisms
        if (showAddEventDialog) {
            EventAddDialog(
                selectedDayLabel = selectedDayLabel,
                selectedCal = selectedCal,
                calendars = calendars,
                onCreateEvent = { calId, title, desc, start, end ->
                    viewModel.createEvent(calId, title, desc, start, end)
                },
                onDismiss = { showAddEventDialog = false }
            )
        }

        if (showCreateCalendarDialog) {
            CalendarContainerDialog(
                onCreateCalendar = { name ->
                    viewModel.createCalendar(name)
                },
                onDismiss = { showCreateCalendarDialog = false }
            )
        }
    }
}
