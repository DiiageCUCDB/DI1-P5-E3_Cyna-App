package com.cyna.app.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ─────────────────────────────────────────────
//  AlertDialog  (shadcn/ui <AlertDialog>)
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style AlertDialog.
 *
 * Designed for confirmation / destructive actions (delete, logout, etc.).
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 *
 * AlertDialog(
 *     open             = open,
 *     onDismiss        = { open = false },
 *     title            = "Are you absolutely sure?",
 *     description      = "This action cannot be undone. This will permanently delete your account.",
 *     confirmLabel     = "Continue",
 *     onConfirm        = { open = false; /* do action */ },
 *     cancelLabel      = "Cancel",
 *     isDestructive    = true
 * )
 * ```
 */
@Composable
fun ShadAlertDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    title: String,
    description: String? = null,
    confirmLabel: String = "Continue",
    cancelLabel: String  = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    properties: DialogProperties = DialogProperties()
) {
    if (!open) return

    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(12.dp),
        containerColor   = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text       = title,
                style      = MaterialTheme.typography.titleLarge.copy(
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color      = MaterialTheme.colorScheme.onSurface
            )
        },
        text = if (description != null) {
            {
                Text(
                    text  = description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else null,
        confirmButton = {
            Button(
                onClick = onConfirm,
                variant = if (isDestructive) ButtonVariant.Destructive else ButtonVariant.Default,
                size = ButtonSize.Sm
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                variant = ButtonVariant.Outline,
                size = ButtonSize.Sm
            ) {
                Text(cancelLabel)
            }
        },
        properties = properties
    )
}

// ─────────────────────────────────────────────
//  Dialog  (shadcn/ui <Dialog> — free-form)
// ─────────────────────────────────────────────

/**
 * shadcn/ui-style full Dialog with a custom content slot.
 *
 * Usage:
 * ```
 * ShadDialog(open = open, onDismiss = { open = false }) {
 *     Column(modifier = Modifier.padding(8.dp)) {
 *         DialogHeader(title = "Edit Profile", description = "Make changes to your profile here.")
 *         Spacer(Modifier.height(16.dp))
 *         // … your form fields …
 *         DialogFooter {
 *             Button(text = "Save changes", onClick = { open = false })
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun ShadDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit
) {
    if (!open) return

    Dialog(onDismissRequest = onDismiss, properties = properties) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            content = content
        )
    }
}

// ─────────────────────────────────────────────
//  DialogHeader / DialogFooter helpers
// ─────────────────────────────────────────────

/**
 * Standard dialog header block (title + optional description).
 */
@Composable
fun DialogHeader(
    title: String,
    description: String? = null,
    modifier: Modifier   = Modifier
) {
    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (description != null) {
            Text(
                text  = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Standard dialog footer — right-aligns its content (action buttons).
 */
@Composable
fun DialogFooter(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier            = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        content             = content
    )
}
