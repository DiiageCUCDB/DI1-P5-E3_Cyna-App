package com.cyna.app.domain.model

// ── User / Profile ────────────────────────────────────────────────────────────

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isConfirmed: Boolean
)

data class Subscription(
    val id: String,
    val productName: String,
    val status: String,
    val duration: String,
    val quantity: Int,
    val unitPrice: Double,
    val endsAt: String
)

// ── Catalog ───────────────────────────────────────────────────────────────────

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val image: String
)

data class Product(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val description: String,
    val priceMonthly: Double,
    val priceYearly: Double,
    val isAvailable: Boolean
)

data class CatalogPage(
    val items: List<Product>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)