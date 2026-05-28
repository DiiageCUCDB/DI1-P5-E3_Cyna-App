package com.cyna.app.data.remote

import com.cyna.app.data.dto.AccountOrderDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

internal class OrderHistoryAPI(private val client: HttpClient) {

    suspend fun getAccountOrders(): List<AccountOrderDto> =
        client.get("account/orders")
            .accept(HttpStatusCode.OK)
            .body()
}