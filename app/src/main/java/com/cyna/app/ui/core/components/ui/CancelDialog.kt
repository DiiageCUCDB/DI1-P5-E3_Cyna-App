package com.cyna.app.ui.core.components.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.domain.model.Subscription
import dev.kindling.core.components.DialogContent
import dev.kindling.core.components.DialogDescription
import dev.kindling.core.components.DialogFooter
import dev.kindling.core.components.DialogHeader
import dev.kindling.core.components.DialogTitle
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KButtonSize
import dev.kindling.core.components.KButtonVariant
import dev.kindling.utils.method.formatDate

// ── Cancel dialog ─────────────────────────────────────────────────────────────

@Composable
fun CancelDialog(
    sub: Subscription,
    cancelling: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    DialogContent(open = true, onDismiss = onDismiss) {
        DialogHeader {
            DialogTitle("Cancel Subscription")
            DialogDescription("Are you sure you want to cancel this subscription? This action cannot be undone.")
        }
        Spacer(Modifier.height(12.dp))
        // Subscription info box
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = cs.error.copy(.07f),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, cs.error.copy(.25f), RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(sub.productName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    buildString {
                        append(sub.planName)
                        append(" · Renews ")
                        append(formatDate(sub.currentPeriodEnd))
                    },
                    fontSize = 11.sp, color = cs.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        DialogFooter {
            KButton("Keep", onClick = onDismiss, variant = KButtonVariant.Outline, size = KButtonSize.Sm)
            KButton(
                onClick = onConfirm,
                variant = KButtonVariant.Destructive,
                size = KButtonSize.Sm,
                isLoading = cancelling
            ) { Text("Cancel Subscription") }
        }
    }
}