package com.abdulaziz733.kinetron.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reusable utility object for date and time formatting across Kinetron screens.
 */
object DateTimeUtils {
    
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateTimeSeconds(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTimeOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatVCardRev(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", java.util.Locale.US)
        return sdf.format(java.util.Date(timestamp))
    }

    fun formatEmailDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatEmailDetailDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDuration(durationSeconds: Long): String {
        if (durationSeconds <= 0) return "0s"
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
    }

    fun formatTimeNoSeconds(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatLogDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
