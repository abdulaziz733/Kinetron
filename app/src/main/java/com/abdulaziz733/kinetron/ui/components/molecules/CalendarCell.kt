package com.abdulaziz733.kinetron.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.ui.screens.CalendarDay
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary

import androidx.compose.ui.tooling.preview.Preview

/**
 * Molecule representing a single grid day cell in the Calendar.
 */
@Composable
fun CalendarCell(
    cell: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 36.dp, height = 50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = when {
                            isSelected -> Color.Black
                            else -> Color.Transparent
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                        color = if (isToday && !isSelected) ToscaPrimary else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cell.day.toString(),
                    fontSize = 14.sp,
                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected -> Color.White
                        isToday -> ToscaPrimary
                        !cell.isCurrentMonth -> Color.LightGray
                        else -> Color.Black
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(3.dp))
            
            if (hasEvent) {
                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 3.dp)
                        .background(
                            color = if (cell.day % 2 == 0) ToscaPrimary else Color(0xFF9C27B0),
                            shape = CircleShape
                        )
                )
            } else {
                Spacer(modifier = Modifier.height(3.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarCellTodayPreview() {
    CalendarCell(
        cell = CalendarDay(24, 6, 2026, true),
        isSelected = false,
        isToday = true,
        hasEvent = true,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CalendarCellSelectedPreview() {
    CalendarCell(
        cell = CalendarDay(15, 6, 2026, true),
        isSelected = true,
        isToday = false,
        hasEvent = false,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CalendarCellNormalPreview() {
    CalendarCell(
        cell = CalendarDay(8, 6, 2026, true),
        isSelected = false,
        isToday = false,
        hasEvent = true,
        onClick = {}
    )
}

