package com.cyna.app.data.remote

import com.cyna.app.data.dto.AccountOrderDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json

internal class OrderHistoryAPI(private val client: HttpClient) {

    // GET /user/orders  →  OrderSummaryDto[]
    suspend fun getAccountOrders(): List<AccountOrderDto> {
        val response = client.get("user/orders")
            .accept(HttpStatusCode.OK)

        val rawBody = response.bodyAsText()
        println("RAW JSON /user/orders: $rawBody")

        return Json { ignoreUnknownKeys = true }.decodeFromString(rawBody)
    }
}