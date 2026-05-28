package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryDto(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val displayOrder: Int,
    val createdAt: String
)

@Serializable
internal data class ProductDto(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val description: String,
    val priceMonthly: Double,
    val priceYearly: Double,
    val isAvailable: Boolean,
    val priority: Int,
    val createdAt: String
)

@Serializable
internal data class CatalogPageDto(
    val items: List<ProductDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)