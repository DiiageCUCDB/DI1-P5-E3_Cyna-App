package com.cyna.app.domain.repository

import com.cyna.app.domain.model.AccountOrder
import com.cyna.app.domain.model.User

interface OrderHistoryRepository {
    suspend fun getAccountOrders(): List<AccountOrder>
}