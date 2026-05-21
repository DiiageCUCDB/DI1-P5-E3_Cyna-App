package com.cyna.app.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputOTP(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    groups: List<Int>? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    cellSize: Dp = 44.dp,
    cellSpacing: Dp = 8.dp,
    separator: @Composable () -> Unit = {
        Text(
            "–",
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Hidden real field
        BasicTextField(
            value         = value,
            onValueChange = { new ->
                if (enabled) onValueChange(new.filter { it.isDigit() }.take(length))
            },
            modifier      = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            enabled        = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush    = SolidColor(Color.Transparent)
        )

        // Visual row — clickable with no ripple to forward focus
        Row(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { focusRequester.requestFocus() },
            horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            if (groups == null) {
                repeat(length) { index ->
                    OTPCell(
                        char     = value.getOrNull(index),
                        isActive = isFocused && index == value.length,
                        isError  = isError,
                        enabled  = enabled,
                        size     = cellSize
                    )
                }
            } else {
                var globalIndex = 0
                groups.forEachIndexed { groupIdx, groupSize ->
                    repeat(groupSize) {
                        OTPCell(
                            char     = value.getOrNull(globalIndex),
                            isActive = isFocused && globalIndex == value.length,
                            isError  = isError,
                            enabled  = enabled,
                            size     = cellSize
                        )
                        globalIndex++
                    }
                    if (groupIdx < groups.lastIndex) separator()
                }
            }
        }
    }
}

@Composable
private fun OTPCell(
    char: Char?,
    isActive: Boolean,
    isError: Boolean,
    enabled: Boolean,
    size: Dp
) {
    val cs = MaterialTheme.colorScheme
    val borderColor = when {
        isError  -> cs.error
        isActive -> cs.primary
        else     -> cs.outline
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(6.dp))
            .border(if (isActive) 2.dp else 1.dp, borderColor, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (char != null) {
            Text(
                text       = char.toString(),
                fontSize   = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign  = TextAlign.Center,
                color      = if (enabled) cs.onBackground else cs.onSurface.copy(alpha = 0.38f)
            )
        } else if (isActive) {
            // Cursor bar
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(cs.primary)
            )
        }
    }
}