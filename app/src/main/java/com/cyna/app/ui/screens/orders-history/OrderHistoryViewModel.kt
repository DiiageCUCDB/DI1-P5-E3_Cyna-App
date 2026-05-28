package com.cyna.app.ui.screens.orders-history

import android.app.Application
import com.cyna.app.domain.model.AccountOrder
import com.cyna.app.domain.model.User
import com.cyna.app.domain.repository.OrderRepository
import com.cyna.app.domain.repository.UserRepository
import com.cyna.app.ui.core.ViewModel
import org.koin.core.component.inject

// ── State ─────────────────────────────────────────────────────────────────────

data class OrderHistoryState(
    val orders: List<AccountOrder> = emptyList(),
    val user: User? = null,
    val loading: Boolean = true,
    val loadingUser: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedYear: String = "all"
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class OrderHistoryViewModel(application: Application) :
    ViewModel<OrderHistoryState>(OrderHistoryState(), application) {

    private val orderHistoryRepository: OrderHistoryRepository by inject()
    private val userRepository: UserRepository by inject()

    init {
        load()
    }

    private fun load() {
        fetchData(
            source = {
                val orders = orderHistoryRepository.getAccountOrders()
                val user   = runCatching { userRepository.getMe() }.getOrNull()
                Pair(orders, user)
            },
            onResult = {
                onSuccess { (orders, user) ->
                    updateState {
                        copy(
                            orders      = orders,
                            user        = user,
                            loading     = false,
                            loadingUser = false
                        )
                    }
                }
                onFailure { e ->
                    updateState {
                        copy(
                            loading     = false,
                            loadingUser = false,
                            error       = e.message ?: "Failed to load orders"
                        )
                    }
                }
            }
        )
    }

    fun onSearchChange(q: String) = updateState { copy(searchQuery = q) }
    fun onYearChange(year: String) = updateState { copy(selectedYear = year) }

    fun retry() {
        updateState { copy(loading = true, loadingUser = true, error = null) }
        load()
    }
}