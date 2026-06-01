package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ProductDto(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val description: String,
    val priceMonthly: Double,
    val priceYearly: Double,
    val status: String,
    val priority: Int,
    val createdAt: String
)