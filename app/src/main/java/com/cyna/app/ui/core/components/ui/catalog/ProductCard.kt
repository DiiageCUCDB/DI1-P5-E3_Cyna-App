package com.cyna.app.ui.core.components.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.cyna.app.domain.model.CatalogProduct
import dev.kindling.core.components.*
import dev.kindling.utils.method.formatPrice

@Composable
fun ProductCard(
    product: CatalogProduct,
    modifier: Modifier = Modifier
) {
    val isAvailable = product.status == "available"
    val cs = MaterialTheme.colorScheme

    KCard(
        modifier = modifier.fillMaxWidth(),
        image = rememberAsyncImagePainter(product.imageUrl)
    ) {
        KCardHeader {
            KCardTitle(text = product.name)
            KCardDescription(text = product.description)
        }

        KCardFooter(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                KBadge(variant = getStatusBadgeVariant(product.status)) {
                    Text(
                        text = product.status.replace("_", " ").replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isAvailable) {
                    Text(
                        text = formatPrice(product.price),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = cs.primary
                    )
                }
            }

            KButton(
                onClick = { /* Navigate to details */ },
                size = KButtonSize.Sm,
                variant = if (isAvailable) KButtonVariant.Default else KButtonVariant.Outline,
                enabled = isAvailable
            ) {
                Text("Details", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun getStatusBadgeVariant(status: String): KBadgeVariant {
    val cs = MaterialTheme.colorScheme
    return when (status) {
        "available" -> KBadgeVariant(
            bg = { Color(0xFF16A34A).copy(alpha = 0.1f) },
            fg = { Color(0xFF16A34A) }
        )
        "unavailable", "out_of_stock" -> KBadgeVariant(
            bg = { cs.error.copy(alpha = 0.1f) },
            fg = { cs.error }
        )
        else -> KBadgeVariant.Secondary
    }
}

@Composable
fun ProductCardSkeleton() {
    KCard(modifier = Modifier.fillMaxWidth()) {
        KCardHeader {
            Skeleton(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
            Skeleton(modifier = Modifier.fillMaxWidth().height(12.dp))
        }
        KCardContent {
            Skeleton(modifier = Modifier.fillMaxWidth().height(12.dp))
        }
        KCardFooter(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Skeleton(modifier = Modifier.width(60.dp).height(18.dp))
                Skeleton(modifier = Modifier.width(80.dp).height(20.dp))
            }
            Skeleton(modifier = Modifier.width(70.dp).height(32.dp))
        }
    }
}
