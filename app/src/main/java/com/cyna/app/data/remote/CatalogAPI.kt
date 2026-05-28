package com.cyna.app.data.remote

import com.cyna.app.data.dto.CatalogPageDto
import com.cyna.app.data.dto.CategoryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

internal class CatalogAPI(private val client: HttpClient) {

    suspend fun getCategories(): List<CategoryDto> =
        client.get("categories")
            .accept(HttpStatusCode.OK)
            .body()

    suspend fun getCatalogProducts(
        query: String = "",
        categoryIds: List<String> = emptyList(),
        maxPrice: Double? = null,
        onlyAvailable: Boolean = false,
        sortBy: String = "relevance",
        page: Int = 1,
        pageSize: Int = 9
    ): CatalogPageDto = client.get("catalog/products") {
        if (query.isNotBlank()) parameter("q", query)
        if (categoryIds.isNotEmpty()) parameter("categoryIds", categoryIds.joinToString(","))
        if (maxPrice != null) parameter("maxPrice", maxPrice.toString())
        if (onlyAvailable) parameter("available", "true")
        parameter("sortBy", sortBy)
        parameter("page", page.toString())
        parameter("pageSize", pageSize.toString())
    }.accept(HttpStatusCode.OK).body()
}