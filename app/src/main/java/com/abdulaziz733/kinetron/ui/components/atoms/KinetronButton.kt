package com.abdulaziz733.kinetron.ui.components.atoms

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary
import com.abdulaziz733.kinetron.ui.theme.ToscaSecondary

/**
 * A highly styled, reusable premium button conforming to Kinetron's color guidelines.
 */
@Composable
fun KinetronButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = ToscaPrimary,
    contentColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = ToscaSecondary.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        content = { content() }
    )
}

@Preview(showBackground = true)
@Composable
fun KinetronButtonPreview() {
    MaterialTheme {
        KinetronButton(onClick = {}) {
            Text("Verify Engine Connect")
        }
    }
}

