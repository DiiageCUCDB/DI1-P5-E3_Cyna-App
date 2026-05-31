package com.cyna.app.domain.model

data class CatalogPage(
    val items: List<Product>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)