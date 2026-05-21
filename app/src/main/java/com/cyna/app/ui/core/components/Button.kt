package com.cyna.app.ui.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  Variants & Sizes  (mirrors shadcn/ui)
// ─────────────────────────────────────────────

enum class ButtonVariant {
    Default,        // primary filled
    Destructive,    // red filled
    Outline,        // bordered, transparent bg
    Secondary,      // secondary filled
    Ghost,          // no bg, no border
    Link            // looks like a text link (underline on click)
}

enum class ButtonSize {
    Default,   // h-9  px-4  py-2
    Sm,        // h-8  px-3  text-xs
    Lg,        // h-10 px-8
    Icon       // h-9  w-9  (square)
}

// ─────────────────────────────────────────────
//  Internal helpers
// ─────────────────────────────────────────────

private data class ButtonColors(
    val container: Color,
    val content: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
    val border: Color? = null
)

@Composable
private fun resolveColors(variant: ButtonVariant): ButtonColors {
    val cs = MaterialTheme.colorScheme
    return when (variant) {
        ButtonVariant.Default -> ButtonColors(
            container        = cs.primary,
            content          = cs.onPrimary,
            disabledContainer = cs.onSurface.copy(alpha = 0.12f),
            disabledContent  = cs.onSurface.copy(alpha = 0.38f)
        )
        ButtonVariant.Destructive -> ButtonColors(
            container        = cs.error,
            content          = cs.onError,
            disabledContainer = cs.onSurface.copy(alpha = 0.12f),
            disabledContent  = cs.onSurface.copy(alpha = 0.38f)
        )
        ButtonVariant.Outline -> ButtonColors(
            container        = Color.Transparent,
            content          = cs.onBackground,
            disabledContainer = Color.Transparent,
            disabledContent  = cs.onSurface.copy(alpha = 0.38f),
            border           = cs.outline
        )
        ButtonVariant.Secondary -> ButtonColors(
            container        = cs.secondaryContainer,
            content          = cs.onSecondaryContainer,
            disabledContainer = cs.onSurface.copy(alpha = 0.12f),
            disabledContent  = cs.onSurface.copy(alpha = 0.38f)
        )
        ButtonVariant.Ghost -> ButtonColors(
            container        = Color.Transparent,
            content          = cs.onBackground,
            disabledContainer = Color.Transparent,
            disabledContent  = cs.onSurface.copy(alpha = 0.38f)
        )
        ButtonVariant.Link -> ButtonColors(
            container        = Color.Transparent,
            content          = cs.primary,
            disabledContainer = Color.Transparent,
            disabledContent  = cs.onSurface.copy(alpha = 0.38f)
        )
    }
}

private data class ButtonDimensions(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val fontSize: Float,
    val width: Dp? = null          // only for Icon size
)

private fun resolveDimensions(size: ButtonSize) = when (size) {
    ButtonSize.Default -> ButtonDimensions(36.dp, 16.dp, 8.dp, 14f)
    ButtonSize.Sm      -> ButtonDimensions(32.dp, 12.dp, 6.dp, 12f)
    ButtonSize.Lg      -> ButtonDimensions(40.dp, 32.dp, 8.dp, 14f)
    ButtonSize.Icon    -> ButtonDimensions(36.dp, 0.dp,  0.dp, 14f, width = 36.dp)
}

// ─────────────────────────────────────────────
//  Main composable
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style Button for Jetpack Compose.
 *
 * Usage:
 * ```
 * Button(onClick = { }) { Text("Click me") }
 * Button(onClick = { }, variant = ButtonVariant.Outline) { Text("Outlined") }
 * Button(onClick = { }, variant = ButtonVariant.Destructive) { Text("Delete") }
 * Button(onClick = { }, size = ButtonSize.Icon) { Icon(Icons.Default.Add, null) }
 * ```
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val colors   = resolveColors(variant)
    val dims     = resolveDimensions(size)
    val shape    = RoundedCornerShape(6.dp)

    val border: BorderStroke? = if (colors.border != null && enabled)
        BorderStroke(1.dp, colors.border)
    else if (colors.border != null && !enabled)
        BorderStroke(1.dp, colors.border.copy(alpha = 0.38f))
    else null

    val containerColor =
        if (enabled) colors.container else colors.disabledContainer
    val contentColor =
        if (enabled) colors.content else colors.disabledContent

    val sizeModifier = if (dims.width != null) {
        modifier.size(dims.width, dims.height)
    } else {
        modifier.height(dims.height)
    }

    Surface(
        onClick = { if (enabled && !isLoading) onClick() },
        modifier = sizeModifier,
        enabled = enabled && !isLoading,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = dims.horizontalPadding, vertical = dims.verticalPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                ProvideTextStyle(
                    MaterialTheme.typography.labelLarge.copy(
                        fontSize = dims.fontSize.sp,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    content()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Convenience overload with a text label
// ─────────────────────────────────────────────

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick    = onClick,
        modifier   = modifier,
        variant    = variant,
        size       = size,
        enabled    = enabled,
        isLoading  = isLoading
    ) {
        Text(text)
    }
}
