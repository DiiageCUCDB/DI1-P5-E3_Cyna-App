package com.cyna.app.data.remote

import com.cyna.app.data.dto.SubscriptionDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

/**
 * Client HTTP pour récupérer les abonnements de l'utilisateur.
 */
internal class ServiceAPI(private val client: HttpClient) {

    /**
     * Interroge l'API (C# ou MockEngine) pour obtenir les abonnements liés au compte.
     * La route est désormais /user/subscriptions.
     */
    suspend fun getUserSubscriptions(): List<SubscriptionDto> {
        val response = client.get("user/subscriptions")
            .accept(HttpStatusCode.OK)

        val rawBody = response.bodyAsText()

        return Json { ignoreUnknownKeys = true }.decodeFromString(rawBody)
    }
}