package com.cyna.app.domain.model

// ---------------------------------------------------------------------------
// OrderItem  ←  OrderItemDto
// ---------------------------------------------------------------------------

data class OrderItem(
    val id: Int,
    val productNameSnapshot: String,
    val planNameSnapshot: String,
    val quantityUsers: Int,
    val quantityDevices: Int
)

// ---------------------------------------------------------------------------
// AccountOrder  ←  OrderSummaryDto
// ---------------------------------------------------------------------------

data class AccountOrder(
    val id: Int,
    val status: String,
    val totalAmount: Double,
    val createdAt: String,
    val invoiceUrl: String?,
    val items: List<OrderItem>
) {
    /**
     * Nom du produit principal = premier item ou "—".
     * Remplace l'ancien champ `productName` du DTO enrichi.
     */
    val primaryProductName: String
        get() = items.firstOrNull()?.productNameSnapshot ?: "—"

    /**
     * Résumé lisible des articles (ex: "Cyna EDR · Shield XDR").
     */
    val itemsSummary: String
        get() = items.joinToString(" · ") { it.productNameSnapshot }
}