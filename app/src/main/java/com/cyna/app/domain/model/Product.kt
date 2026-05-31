package com.cyna.app.domain.model

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