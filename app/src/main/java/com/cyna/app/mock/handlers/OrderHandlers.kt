package com.cyna.app.mock.handlers

import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*

// ---------------------------------------------------------------------------
// Subscriptions — routes internes conservées (/subscriptions/:id)
// La route principale GET /user/subscriptions est dans UserHandlers.kt
// ---------------------------------------------------------------------------

val subscriptionHandlers: List<MockHandler> = listOf(

    // DELETE /subscriptions/:id  (ancienne route interne)
    MockHandler(
        method = HttpMethod.Delete,
        path = "/subscriptions/:id",
        status = HttpStatusCode.NoContent,
        resolver = { _, _ -> null }
    ),
)

// ---------------------------------------------------------------------------
// Account order handlers — toutes les routes sont maintenant dans UserHandlers
// Ce fichier reste pour compatibilité avec MockInitializer mais est vide.
// ---------------------------------------------------------------------------

val accountOrderHandlers: List<MockHandler> = emptyList()