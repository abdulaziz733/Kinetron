package com.abdulaziz733.kinetron.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ToscaTertiary,
    secondary = ToscaSecondary,
    tertiary = ToscaPrimary,
    background = Color(0xFF001212),
    surface = Color(0xFF001A1A),
    onPrimary = Color(0xFF001212),
    onSecondary = Color(0xFF001212),
    onBackground = Color(0xFFE2F3F3),
    onSurface = Color(0xFFE2F3F3)
)

private val LightColorScheme = lightColorScheme(
    primary = ToscaPrimary,
    secondary = ToscaSecondary,
    tertiary = ToscaTertiary,
    background = ToscaLightBg,
    surface = ToscaSurface,
    onPrimary = ToscaOnPrimary,
    onSecondary = ToscaOnSecondary,
    onBackground = ToscaOnBg,
    onSurface = ToscaOnSurface
)

@Composable
fun KinetronTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor = false by default to enforce the custom Tosca brand theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}