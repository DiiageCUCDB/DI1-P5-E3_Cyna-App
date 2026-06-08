package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

// ---------------------------------------------------------------------------
// OrderItemDto  (nested in OrderSummaryDto)
// { id, productNameSnapshot, planNameSnapshot, quantityUsers, quantityDevices }
// ---------------------------------------------------------------------------

@Serializable
internal data class OrderItemDto(
    val id: Int,
    val productNameSnapshot: String,
    val planNameSnapshot: String,
    val quantityUsers: Int,
    val quantityDevices: Int
)

// ---------------------------------------------------------------------------
// OrderSummaryDto  (GET /user/orders items)
// { id, status, totalAmount, createdAt, invoiceUrl, items }
// ---------------------------------------------------------------------------

@Serializable
internal data class AccountOrderDto(
    val id: Int,
    val status: String,
    val totalAmount: Double,
    val createdAt: String,
    val invoiceUrl: String? = null,
    val items: List<OrderItemDto> = emptyList()
)