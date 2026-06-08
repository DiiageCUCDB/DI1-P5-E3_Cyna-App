package com.cyna.app.ui.core.components.ui.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyna.app.domain.model.AccountOrder
import dev.kindling.core.components.KBadge
import dev.kindling.core.components.KBadgeVariant
import dev.kindling.core.components.KButton
import dev.kindling.core.components.KButtonSize
import dev.kindling.core.components.KButtonVariant
import dev.kindling.core.components.Skeleton
import dev.kindling.utils.method.formatDate
import dev.kindling.utils.method.formatPrice

// ── Status badge — PascalCase (enum .NET) ────────────────────────────────────

@Composable
private fun statusVariant(status: String): KBadgeVariant {
    val cs = MaterialTheme.colorScheme
    return when (status) {
        "Paid"    -> KBadgeVariant(
            bg = { Color(0xFF166534).copy(alpha = .12f) },
            fg = { Color(0xFF166534) }
        )
        "Pending" -> KBadgeVariant(
            bg = { Color(0xFF78350F).copy(alpha = .12f) },
            fg = { Color(0xFF92400E) }
        )
        "Failed",
        "Refunded" -> KBadgeVariant(
            bg = { cs.error.copy(alpha = .10f) },
            fg = { cs.error }
        )
        else -> KBadgeVariant(
            bg = { cs.surfaceVariant },
            fg = { cs.onSurfaceVariant }
        )
    }
}

// ── Order row ─────────────────────────────────────────────────────────────────
//
// AccountOrder (v1) :
//   { id, status, totalAmount, createdAt, invoiceUrl, items: List<OrderItem> }
//   computed: primaryProductName, itemsSummary

@Composable
fun OrderRow(order: AccountOrder, isLast: Boolean) {
    val cs = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Gauche : nom principal + statut + résumé des autres items
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        // Nom du premier produit — computed property
                        text = order.primaryProductName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    KBadge(variant = statusVariant(order.status)) {
                        Text(
                            // Libellé localisé du statut
                            text = localizeStatus(order.status),
                            fontSize = 10.sp
                        )
                    }
                }

                // Date de commande
                Text(
                    text = formatDate(order.createdAt),
                    fontSize = 11.sp,
                    color = cs.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Résumé des items si plusieurs produits
                if (order.items.size > 1) {
                    Text(
                        text = order.itemsSummary,
                        fontSize = 11.sp,
                        color = cs.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Nombre d'articles
                if (order.items.isNotEmpty()) {
                    Text(
                        text = "${order.items.size} article${if (order.items.size > 1) "s" else ""}",
                        fontSize = 10.sp,
                        color = cs.onSurfaceVariant.copy(.7f)
                    )
                }
            }

            // Droite : montant + facture
            Column(horizontalAlignment = Alignment.End) {
                // totalAmount depuis OrderSummaryDto
                Text(
                    text = formatPrice(order.totalAmount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = cs.onSurface
                )
                if (order.invoiceUrl != null) {
                    Spacer(Modifier.height(4.dp))
                    KButton(
                        onClick = { /* TODO: ouvrir invoiceUrl */ },
                        variant = KButtonVariant.Outline,
                        size = KButtonSize.Xs
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text("PDF", fontSize = 10.sp)
                    }
                }
            }
        }

        if (!isLast) {
            HorizontalDivider(
                color = cs.outline.copy(.25f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ── Localisation du statut PascalCase ────────────────────────────────────────

private fun localizeStatus(status: String): String = when (status) {
    "Paid"     -> "Payé"
    "Pending"  -> "En attente"
    "Failed"   -> "Échoué"
    "Refunded" -> "Remboursé"
    else       -> status
}

// ── Skeleton row ──────────────────────────────────────────────────────────────

@Composable
fun OrderRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Skeleton(modifier = Modifier.fillMaxWidth(.55f).height(12.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(.75f).height(10.dp))
            Skeleton(modifier = Modifier.fillMaxWidth(.45f).height(10.dp))
        }
        Skeleton(modifier = Modifier.width(56.dp).height(18.dp))
    }
}