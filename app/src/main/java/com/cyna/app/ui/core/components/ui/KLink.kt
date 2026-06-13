package com.cyna.app.ui.core.components.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun KLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium.copy(
            textDecoration = TextDecoration.Underline
        ),
        modifier = modifier.clickable(onClick = onClick)
    )
}
