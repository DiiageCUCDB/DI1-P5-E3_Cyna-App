package com.cyna.app.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  EmptyMedia variant
// ─────────────────────────────────────────────

enum class EmptyMediaVariant { Icon, Image, Avatar }

// ─────────────────────────────────────────────
//  Empty  (root)
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style Empty state root.
 *
 * Composition:
 * ```
 * Empty
 * ├── EmptyHeader
 * │   ├── EmptyMedia
 * │   ├── EmptyTitle
 * │   └── EmptyDescription
 * └── EmptyContent
 * ```
 *
 * Usage — default:
 * ```
 * Empty {
 *     EmptyHeader {
 *         EmptyMedia { Icon(Icons.Outlined.FolderOpen, null) }
 *         EmptyTitle("No Projects Yet")
 *         EmptyDescription("Get started by creating your first project.")
 *     }
 *     EmptyContent { Button(text = "Create Project", onClick = { }) }
 * }
 * ```
 */
@Composable
fun Empty(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        content = content
    )
}

// ─────────────────────────────────────────────
//  EmptyHeader
// ─────────────────────────────────────────────

@Composable
fun EmptyHeader(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

// ─────────────────────────────────────────────
//  EmptyMedia
// ─────────────────────────────────────────────

/**
 * Icon variant wraps content in a rounded square with [iconBoxColor] background.
 * Image / Avatar variants are unstyled / circular respectively.
 */
@Composable
fun EmptyMedia(
    modifier: Modifier = Modifier,
    variant: EmptyMediaVariant = EmptyMediaVariant.Icon,
    size: Dp = 56.dp,
    iconBoxColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable BoxScope.() -> Unit
) {
    val boxModifier = when (variant) {
        EmptyMediaVariant.Icon -> modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(iconBoxColor)
            .padding(12.dp)

        EmptyMediaVariant.Avatar -> modifier
            .size(size)
            .clip(CircleShape)

        EmptyMediaVariant.Image -> modifier.size(size)
    }

    Box(modifier = boxModifier, contentAlignment = Alignment.Center, content = content)
}

// ─────────────────────────────────────────────
//  EmptyTitle
// ─────────────────────────────────────────────

@Composable
fun EmptyTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text, modifier = modifier,
        fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center
    )
}

// ─────────────────────────────────────────────
//  EmptyDescription
// ─────────────────────────────────────────────

@Composable
fun EmptyDescription(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text, modifier = modifier.padding(horizontal = 16.dp),
        fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
    )
}

// ─────────────────────────────────────────────
//  EmptyContent
// ─────────────────────────────────────────────

@Composable
fun EmptyContent(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Spacer(Modifier.height(8.dp))
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        content = content
    )
}

// ─────────────────────────────────────────────
//  EmptyAvatar  (initials-based avatar for Avatar variant)
// ─────────────────────────────────────────────

/**
 * A simple initials avatar — use inside [EmptyMedia] with [EmptyMediaVariant.Avatar].
 *
 * ```
 * EmptyMedia(variant = EmptyMediaVariant.Avatar) {
 *     EmptyAvatar(initials = "LR")
 * }
 * ```
 */
@Composable
fun EmptyAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(),
            fontSize = (size.value * 0.3f).sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

// ─────────────────────────────────────────────
//  EmptyState  (convenience preset)
// ─────────────────────────────────────────────

/**
 * Ready-to-use empty state with icon, title, description, and optional CTAs.
 *
 * ```
 * EmptyState(
 *     icon        = Icons.Outlined.FolderOpen,
 *     title       = "No projects yet",
 *     description = "Create your first project to get started.",
 *     actionLabel = "Create Project",
 *     onAction    = { }
 * )
 * ```
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    secondaryActionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null
) {
    Empty(modifier = modifier) {
        EmptyHeader {
            Spacer(Modifier.height(8.dp))
            EmptyMedia(variant = EmptyMediaVariant.Icon) {
                Icon(
                    imageVector = icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(4.dp))
            EmptyTitle(text = title)
            EmptyDescription(text = description)
        }

        Spacer(Modifier.height(8.dp))
    }
}