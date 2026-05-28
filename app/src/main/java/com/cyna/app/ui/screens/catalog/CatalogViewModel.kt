package com.cyna.app.ui.screens.catalog

import android.app.Application
import com.cyna.app.data.remote.CatalogAPI
import com.cyna.app.domain.model.Category
import com.cyna.app.domain.model.CatalogPage
import com.cyna.app.domain.model.Product
import com.cyna.app.ui.core.ViewModel
import org.koin.core.component.inject

// ── State ─────────────────────────────────────────────────────────────────────

data class CatalogState(
    // results
    val products: List<Product> = emptyList(),
    val total: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val loadingProducts: Boolean = true,
    // categories
    val categories: List<Category> = emptyList(),
    val loadingCategories: Boolean = true,
    // filters
    val searchQuery: String = "",
    val selectedCategories: List<String> = emptyList(),
    val maxPrice: Double? = null,
    val onlyAvailable: Boolean = false,
    val sortBy: String = "relevance"
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class CatalogViewModel(application: Application) : ViewModel<CatalogState>(CatalogState(), application) {

    private val catalogAPI: CatalogAPI by inject()

    companion object {
        const val PAGE_SIZE = 9
    }

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        fetchData(
            source = {
                catalogAPI.getCategories().map { Category(it.id, it.name, it.description, it.image) }
            },
            onResult = {
                onSuccess { cats -> updateState { copy(categories = cats, loadingCategories = false) } }
                onFailure { updateState { copy(loadingCategories = false) } }
            }
        )
    }

    fun loadProducts(page: Int = 1) {
        val s = state.value
        updateState { copy(loadingProducts = true, currentPage = page) }
        fetchData(
            source = {
                val page2 = catalogAPI.getCatalogProducts(
                    query = s.searchQuery,
                    categoryIds = s.selectedCategories,
                    maxPrice = s.maxPrice,
                    onlyAvailable = s.onlyAvailable,
                    sortBy = s.sortBy,
                    page = page,
                    pageSize = PAGE_SIZE
                )
                CatalogPage(
                    items = page2.items.map {
                        Product(it.id, it.categoryId, it.imageUrl, it.name, it.description, it.priceMonthly, it.priceYearly, it.isAvailable)
                    },
                    total = page2.total,
                    page = page2.page,
                    totalPages = page2.totalPages
                )
            },
            onResult = {
                onSuccess { pg ->
                    updateState {
                        copy(
                            products = pg.items,
                            total = pg.total,
                            currentPage = pg.page,
                            totalPages = pg.totalPages,
                            loadingProducts = false
                        )
                    }
                }
                onFailure { updateState { copy(loadingProducts = false) } }
            }
        )
    }

    fun onSearchChange(q: String) {
        updateState { copy(searchQuery = q) }
        loadProducts(page = 1)
    }

    fun onCategoryToggle(id: String) {
        val current = state.value.selectedCategories
        val next = if (id in current) current - id else current + id
        updateState { copy(selectedCategories = next) }
        loadProducts(page = 1)
    }

    fun onMaxPriceChange(price: Double?) {
        updateState { copy(maxPrice = price) }
        loadProducts(page = 1)
    }

    fun onAvailableToggle(v: Boolean) {
        updateState { copy(onlyAvailable = v) }
        loadProducts(page = 1)
    }

    fun onSortChange(sort: String) {
        updateState { copy(sortBy = sort) }
        loadProducts(page = 1)
    }

    fun onPageChange(page: Int) = loadProducts(page)

    fun resetFilters() {
        updateState {
            copy(
                searchQuery = "",
                selectedCategories = emptyList(),
                maxPrice = null,
                onlyAvailable = false,
                sortBy = "relevance"
            )
        }
        loadProducts(page = 1)
    }
}