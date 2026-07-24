package com.abdulaziz733.kinetron.ui.components.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Reusable animated circular loading progress indicator.
 */
@Composable
fun KinetronLoader(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    CircularProgressIndicator(
        modifier = modifier.size(24.dp),
        color = color,
        strokeWidth = 2.5.dp
    )
}

@Preview(showBackground = true)
@Composable
fun KinetronLoaderPreview() {
    MaterialTheme {
        KinetronLoader(color = Color.DarkGray)
    }
}

