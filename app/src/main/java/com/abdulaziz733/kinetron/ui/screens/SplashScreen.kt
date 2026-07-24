package com.abdulaziz733.kinetron.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdulaziz733.kinetron.R
import com.abdulaziz733.kinetron.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation states
    val scale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Run animations sequentially
    LaunchedEffect(key1 = true) {
        // Logo bounce animation
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = tween(durationMillis = 800)
        )
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 200)
        )
        // Fade in subtitle/title text
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        // Wait a short moment before navigating
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ToscaPrimary, ToscaSecondary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_kinetron_splash),
                contentDescription = "Kinetron Logo",
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(Color(0xFF008080))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Title
            Text(
                text = "Kinetron",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.scale(scale.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Centralizing Your OS Ecosystem",
                color = Color.White.copy(alpha = textAlpha.value * 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
