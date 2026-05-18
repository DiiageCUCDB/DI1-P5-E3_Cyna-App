package com.diiage.template.ui.core.components.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
//  Shimmer brush (shared utility)
// ─────────────────────────────────────────────

/**
 * Returns a moving shimmer [Brush] — add it as a [Modifier.background] to any
 * composable to get the classic skeleton animation.
 */
@Composable
fun shimmerBrush(
    baseColor:      Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.surface,
    durationMillis: Int   = 1_200
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue   = 0f,
        targetValue    = 1_000f,
        animationSpec  = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start  = Offset(translateAnim - 300f, translateAnim - 300f),
        end    = Offset(translateAnim,        translateAnim)
    )
}

// ─────────────────────────────────────────────
//  Core Skeleton composable
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style Skeleton.
 *
 * A shimmering loading placeholder. Drop it anywhere a real element will
 * eventually appear.
 *
 * Usage:
 * ```
 * // Simple rectangle
 * Skeleton(modifier = Modifier.fillMaxWidth().height(20.dp))
 *
 * // Circle (avatar placeholder)
 * Skeleton(modifier = Modifier.size(40.dp), shape = CircleShape)
 *
 * // Rounded card
 * Skeleton(modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp))
 * ```
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape:    Shape    = RoundedCornerShape(4.dp),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush())
    )
}

// ─────────────────────────────────────────────
//  Pre-built skeleton layouts
// ─────────────────────────────────────────────

/**
 * A skeleton row with a circular avatar placeholder and two text lines —
 * perfect for list items.
 *
 * ```
 * SkeletonListItem()
 * ```
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier,
    avatarSize: Dp = 40.dp,
) {
    Row(
        modifier            = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Skeleton(
            modifier = Modifier.size(avatarSize),
            shape    = CircleShape
        )

        // Text lines
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Skeleton(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp))
        }
    }
}

/**
 * A skeleton card placeholder.
 *
 * ```
 * SkeletonCard()
 * ```
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    imageHeight: Dp    = 180.dp
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Image area
        Skeleton(
            modifier = Modifier.fillMaxWidth().height(imageHeight),
            shape    = RoundedCornerShape(8.dp)
        )
        // Title
        Skeleton(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
        // Subtitle lines
        Skeleton(modifier = Modifier.fillMaxWidth().height(12.dp))
        Skeleton(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp))
    }
}
