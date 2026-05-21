package com.cyna.app.ui.core.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SpinnerSize { Sm, Default, Lg, Xl }

private fun SpinnerSize.toDp(): Dp = when (this) {
    SpinnerSize.Sm      -> 16.dp
    SpinnerSize.Default -> 24.dp
    SpinnerSize.Lg      -> 32.dp
    SpinnerSize.Xl      -> 48.dp
}

private fun SpinnerSize.strokeWidth(): Dp = when (this) {
    SpinnerSize.Sm      -> 2.dp
    SpinnerSize.Default -> 2.5.dp
    SpinnerSize.Lg      -> 3.dp
    SpinnerSize.Xl      -> 4.dp
}

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    size: SpinnerSize = SpinnerSize.Default,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = color.copy(alpha = 0.15f),
    label: String? = null,
) {
    val dp       = size.toDp()
    val strokeDp = size.strokeWidth()

    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val angle by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinnerAngle"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(modifier = Modifier.size(dp)) {
            val strokePx = strokeDp.toPx()
            val stroke   = Stroke(width = strokePx, cap = StrokeCap.Round)
            val inset    = strokePx / 2f
            val arcSize  = Size(
                width  = this.size.width  - strokePx,
                height = this.size.height - strokePx
            )
            val topLeft  = Offset(inset, inset)

            // Track (full circle)
            drawArc(
                color      = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter  = false,
                style      = stroke,
                topLeft    = topLeft,
                size       = arcSize
            )
            // Spinning arc (~quarter circle)
            drawArc(
                color      = color,
                startAngle = angle,
                sweepAngle = 80f,
                useCenter  = false,
                style      = stroke,
                topLeft    = topLeft,
                size       = arcSize
            )
        }

        if (label != null) {
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun SpinnerOverlay(
    modifier: Modifier = Modifier.fillMaxSize(),
    size: SpinnerSize = SpinnerSize.Lg,
    label: String? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Spinner(size = size, label = label)
    }
}