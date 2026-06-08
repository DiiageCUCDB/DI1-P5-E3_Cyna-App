package com.cyna.app.ui.core.components.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.domain.model.Subscription
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KButtonSize
import dev.kindling.core.components.KButtonVariant
import dev.kindling.utils.method.formatDate

// ── Subscription item ─────────────────────────────────────────────────────────
//
// Subscription domain model (v1) :
//   { id, status, productName, planName,
//     currentPeriodStart, currentPeriodEnd, autoRenew }

@Composable
fun SubscriptionRow(sub: Subscription, onCancel: () -> Unit) {
    val cs = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = cs.primary.copy(.05f),
        border = BorderStroke(1.dp, cs.primary.copy(.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Nom produit + plan
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = sub.productName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    // Badge plan
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = cs.primary.copy(.12f)
                    ) {
                        Text(
                            text = sub.planName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = cs.primary
                        )
                    }
                    // Badge auto-renouvellement
                    if (sub.autoRenew) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF16A34A).copy(.10f)
                        ) {
                            Text(
                                text = "Auto",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(3.dp))

                // Date de renouvellement depuis currentPeriodEnd
                Text(
                    text = "Renouvellement le ${formatDate(sub.currentPeriodEnd)}",
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(8.dp))

            KButton(
                "Cancel",
                onClick = onCancel,
                variant = KButtonVariant.Destructive,
                size = KButtonSize.Sm
            )
        }
    }
}