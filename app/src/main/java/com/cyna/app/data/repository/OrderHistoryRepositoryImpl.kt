package com.cyna.app.data.repository

import com.cyna.app.data.remote.OrderHistoryAPI
import com.cyna.app.domain.model.AccountOrder
import com.cyna.app.domain.model.OrderItem
import com.cyna.app.domain.repository.OrderHistoryRepository

internal class OrderHistoryRepositoryImpl(
    private val orderHistoryAPI: OrderHistoryAPI
) : OrderHistoryRepository {

    // OrderSummaryDto → AccountOrder
    override suspend fun getAccountOrders(): List<AccountOrder> =
        orderHistoryAPI.getAccountOrders().map { dto ->
            AccountOrder(
                id          = dto.id,
                status      = dto.status,
                totalAmount = dto.totalAmount,
                createdAt   = dto.createdAt,
                invoiceUrl  = dto.invoiceUrl,
                items       = dto.items.map { item ->
                    OrderItem(
                        id                   = item.id,
                        productNameSnapshot  = item.productNameSnapshot,
                        planNameSnapshot     = item.planNameSnapshot,
                        quantityUsers        = item.quantityUsers,
                        quantityDevices      = item.quantityDevices
                    )
                }
            )
        }
}