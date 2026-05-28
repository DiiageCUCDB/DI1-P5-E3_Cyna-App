package com.cyna.app.data.repository

import com.cyna.app.data.remote.OrderHistoryAPI
import com.cyna.app.domain.model.AccountOrder
import com.cyna.app.domain.repository.OrderHistoryRepository

internal class OrderHistoryRepositoryImpl(
    private val orderHistoryAPI: OrderHistoryAPI
) : OrderHistoryRepository {

    override suspend fun getAccountOrders(): List<AccountOrder> =
        orderHistoryAPI.getAccountOrders().map { dto ->
            AccountOrder(
                id            = dto.id,
                status        = dto.status,
                statusLabel   = dto.statusLabel,
                productName   = dto.productName,
                total         = dto.total,
                type          = dto.type,
                paymentLast4  = dto.paymentLast4,
                paymentMethod = dto.paymentMethod,
                invoiceUrl    = dto.invoiceUrl,
                createdAt     = dto.createdAt
            )
        }
}