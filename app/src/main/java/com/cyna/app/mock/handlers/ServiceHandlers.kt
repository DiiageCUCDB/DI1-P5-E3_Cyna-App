package com.cyna.app.mock.handlers

import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlin.random.Random

/**
 * Intercepte les requêtes de télémétrie des services.
 */
val serviceHandlers: List<MockHandler> = listOf(
    MockHandler(
        method = HttpMethod.Get,
        path = "/user/services",
        status = HttpStatusCode.OK,
        resolver = { _, _ ->
            // Simule qu'un utilisateur possède entre 2 et 4 logiciels actifs
            MockFactories.makeMany(Random.nextInt(2, 5)) {
                MockFactories.makePurchasedService()
            }
        }
    )
)