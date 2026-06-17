package com.cyna.app.data.remote

import com.cyna.app.data.dto.PurchasedServiceDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

/**
 * Client HTTP spécifique pour la gestion des services/télémétrie.
 */
internal class ServiceAPI(private val client: HttpClient) {

    /**
     * Appelle l'API GET /user/services pour récupérer l'état des logiciels.
     * Utilise `accept()` pour lever une exception si le statut n'est pas 200 OK.
     */
    suspend fun getPurchasedServices(): List<PurchasedServiceDto> {
        val response = client.get("user/services")
            .accept(HttpStatusCode.OK)

        val rawBody = response.bodyAsText()
        // Log utile pour débugger le JSON intercepté par le mock
        println("RAW JSON /user/services: $rawBody")

        return Json { ignoreUnknownKeys = true }.decodeFromString(rawBody)
    }
}