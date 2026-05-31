package com.cyna.app.domain.repository

import com.cyna.app.domain.model.AccountOrder

interface OrderHistoryRepository {
    suspend fun getAccountOrders(): List<AccountOrder>
}