package com.cyna.app.ui.core.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// ─────────────────────────────────────────────
//  Toast data model
// ─────────────────────────────────────────────

enum class ToastType { Default, Success, Error, Warning, Info }

data class ToastData(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val description: String? = null,
    val type: ToastType      = ToastType.Default,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val durationMs: Long     = 4_000L
)

// ─────────────────────────────────────────────
//  Toaster state (singleton-like, use via ToasterState)
// ─────────────────────────────────────────────

object Toaster {
    private val _flow = MutableSharedFlow<ToastData>(extraBufferCapacity = 8)
    val flow = _flow.asSharedFlow()

    fun show(
        message: String,
        description: String? = null,
        type: ToastType      = ToastType.Default,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        durationMs: Long     = 4_000L
    ) {
        _flow.tryEmit(
            ToastData(
                message     = message,
                description = description,
                type        = type,
                actionLabel = actionLabel,
                onAction    = onAction,
                durationMs  = durationMs
            )
        )
    }

    fun success(message: String, description: String? = null) =
        show(message, description, ToastType.Success)

    fun error(message: String, description: String? = null) =
        show(message, description, ToastType.Error)

    fun warning(message: String, description: String? = null) =
        show(message, description, ToastType.Warning)

    fun info(message: String, description: String? = null) =
        show(message, description, ToastType.Info)
}

// ─────────────────────────────────────────────
//  ToasterHost  — add this once at root level
// ─────────────────────────────────────────────

/**
 * shadcn/ui Sonner-style toast host.
 *
 * Place this **once** at the top of your composable tree (inside [AppTheme]),
 * then call `Toaster.success(…)` / `Toaster.error(…)` from anywhere.
 *
 * Usage in App.kt:
 * ```
 * @Composable
 * fun App() {
 *     val navController = rememberNavController()
 *     AppTheme {
 *         Box(Modifier.fillMaxSize()) {
 *             Surface(…) { NavHost(navController) }
 *             ToasterHost()              // <── add this
 *         }
 *     }
 * }
 * ```
 *
 * Trigger from anywhere:
 * ```
 * Toaster.success("Profile saved")
 * Toaster.error("Upload failed", "Please try again.")
 * Toaster.show(
 *     message     = "Event created",
 *     actionLabel = "Undo",
 *     onAction    = { /* undo */ }
 * )
 * ```
 */
@Composable
fun ToasterHost(
    modifier: Modifier = Modifier,
    maxVisible: Int    = 3
) {
    val toasts = remember { mutableStateListOf<ToastData>() }

    LaunchedEffect(Unit) {
        Toaster.flow.collect { toast ->
            toasts.add(0, toast)
            if (toasts.size > maxVisible) toasts.removeLastOrNull()
        }
    }

    Box(
        modifier        = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            toasts.forEach { toast ->
                key(toast.id) {
                    ToastItem(
                        toast   = toast,
                        onClose = { toasts.remove(toast) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Individual toast item
// ─────────────────────────────────────────────

@Composable
private fun ToastItem(
    toast: ToastData,
    onClose: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(toast.id) {
        delay(toast.durationMs)
        visible = false
        delay(300)
        onClose()
    }

    AnimatedVisibility(
        visible  = visible,
        enter    = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
        exit     = slideOutVertically(tween(300)) { it } + fadeOut(tween(300))
    ) {
        ToastCard(toast = toast, onClose = {
            visible = false
            onClose()
        })
    }
}

@Composable
private fun ToastCard(toast: ToastData, onClose: () -> Unit) {
    val cs = MaterialTheme.colorScheme

    val (bgColor, iconTint, icon) = when (toast.type) {
        ToastType.Success -> Triple(Color(0xFF166534), Color(0xFF4ADE80), Icons.Default.Check)
        ToastType.Error   -> Triple(cs.errorContainer, cs.error, Icons.Default.Close)
        ToastType.Warning -> Triple(Color(0xFF78350F), Color(0xFFFBBF24), Icons.Default.Warning)
        ToastType.Info    -> Triple(cs.primaryContainer, cs.primary, Icons.Default.Info)
        ToastType.Default -> Triple(cs.surface, cs.onSurface, null)
    }

    val textColor = when (toast.type) {
        ToastType.Success -> Color(0xFFDCFCE7)
        ToastType.Error   -> cs.onErrorContainer
        ToastType.Warning -> Color(0xFFFEF3C7)
        ToastType.Info    -> cs.onPrimaryContainer
        ToastType.Default -> cs.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon
        if (icon != null) {
            Icon(
                imageVector  = icon,
                contentDescription = null,
                tint         = iconTint,
                modifier     = Modifier.size(18.dp)
            )
        }

        // Text block
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = toast.message,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = textColor
            )
            if (toast.description != null) {
                Text(
                    text     = toast.description,
                    fontSize = 12.sp,
                    color    = textColor.copy(alpha = 0.8f)
                )
            }
        }

        // Action button
        if (toast.actionLabel != null && toast.onAction != null) {
            TextButton(onClick = { toast.onAction.invoke(); onClose() }) {
                Text(
                    toast.actionLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color    = iconTint
                )
            }
        }

        // Close button
        IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Dismiss",
                tint     = textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
