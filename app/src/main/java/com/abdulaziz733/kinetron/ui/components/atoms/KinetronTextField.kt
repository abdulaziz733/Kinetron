package com.abdulaziz733.kinetron.ui.components.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abdulaziz733.kinetron.ui.theme.ToscaOnBg
import com.abdulaziz733.kinetron.ui.theme.ToscaPrimary

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Standardized input text field enforcing contrast, styling, and rounded corners.
 */
@Composable
fun KinetronTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ToscaOnBg,
            unfocusedTextColor = ToscaOnBg,
            focusedBorderColor = ToscaPrimary,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = ToscaPrimary,
            unfocusedLabelColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray,
            focusedLeadingIconColor = ToscaPrimary,
            unfocusedLeadingIconColor = Color.Gray
        )
    )
}

@Preview(showBackground = true)
@Composable
fun KinetronTextFieldPreview() {
    MaterialTheme {
        KinetronTextField(
            value = "example@kinetron.io",
            onValueChange = {},
            placeholder = "Enter Email"
        )
    }
}

