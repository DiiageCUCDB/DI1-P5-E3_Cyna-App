package com.cyna.app.mock.handlers

import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.factories.MockUser
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ---------------------------------------------------------------------------
// In-memory user state — mirrors user.js
// ---------------------------------------------------------------------------

private var _currentUser: MockUser = MockFactories.makeDemoUser()

/** Probability of simulated failure on mutation endpoints (0–1). */
private const val FAILURE_RATE = 0.3

private val failureMessages = listOf(
    "Erreur serveur interne — veuillez réessayer.",
    "Session expirée — reconnectez-vous.",
    "Données invalides reçues par le serveur.",
    "Service temporairement indisponible (503).",
)

private fun randomFailure() {
    if (Math.random() < FAILURE_RATE) error(failureMessages.random())
}

@Serializable
private data class MessageResponse(val message: String)

// ---------------------------------------------------------------------------
// User handlers — mirrors handlers/user.js
// ---------------------------------------------------------------------------

val userHandlers: List<MockHandler> = listOf(

    // PUT /user/profile — may fail randomly
    MockHandler(
        method = HttpMethod.Put,
        path = "/user/profile",
        resolver = { _, body ->
            randomFailure()
            // Partially update from JSON body if provided
            if (body != null) {
                runCatching {
                    val json = Json.parseToJsonElement(body).jsonObject
                    _currentUser = _currentUser.copy(
                        name  = json["name"]?.jsonPrimitive?.content  ?: _currentUser.name,
                        email = json["email"]?.jsonPrimitive?.content ?: _currentUser.email,
                    )
                }
            }
            _currentUser
        }
    ),

    // PUT /user/password — validates body, may fail randomly
    MockHandler(
        method = HttpMethod.Put,
        path = "/user/password",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            if (json?.get("currentPassword")?.jsonPrimitive?.content.isNullOrBlank()) {
                error("Le mot de passe actuel est requis.")
            }
            randomFailure()
            MessageResponse("Mot de passe mis à jour avec succès.")
        }
    ),

    // GET /user/subscriptions
    MockHandler(
        method = HttpMethod.Get,
        path = "/user/subscriptions",
        resolver = { _, _ ->
            MockFactories.makeMany((1..3).random()) {
                MockFactories.makeSubscription(status = "active")
            }
        }
    ),
)