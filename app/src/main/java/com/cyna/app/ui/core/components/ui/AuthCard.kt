package com.cyna.app.ui.core.components.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * White card that wraps auth screen content — mirrors the
 * `rounded-2xl border border-gray-100 bg-white p-8 shadow-sm` card from the web.
 */
@Composable
fun AuthCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            content = content
        )
    }
}