package com.abdulaziz733.kinetron.ui.screens

/**
 * Data representation of a single grid cell in the month-view calendar.
 */
data class CalendarDay(
    val day: Int,
    val month: Int,
    val year: Int,
    val isCurrentMonth: Boolean
)
