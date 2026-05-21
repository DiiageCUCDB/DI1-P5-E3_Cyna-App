package com.cyna.app.ui.core.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * shadcn/ui-style Label.
 *
 * A small, medium-weight text label — typically placed above form fields.
 * Automatically grays out when [disabled] is true (mirrors the peer-disabled
 * behaviour of the shadcn/ui CSS implementation).
 *
 * Usage:
 * ```
 * Label(text = "Email address")
 * Label(text = "Password", disabled = true)
 * ```
 */
@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    style: TextStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize   = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    color: Color = if (disabled)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    else
        MaterialTheme.colorScheme.onBackground
) {
    Text(
        text     = text,
        style    = style,
        color    = color,
        modifier = modifier
    )
}
