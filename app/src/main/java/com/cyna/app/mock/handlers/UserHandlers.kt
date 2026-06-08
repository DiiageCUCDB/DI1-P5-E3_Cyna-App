package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.factories.MockUser
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ---------------------------------------------------------------------------
// État en mémoire — miroir de handlers/user.js
// ---------------------------------------------------------------------------

private var _currentUser: MockUser = MockFactories.makeDemoUser()

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

// ---------------------------------------------------------------------------
// User handlers — miroir de handlers/user.js
// ---------------------------------------------------------------------------

val userHandlers: List<MockHandler> = listOf(

    // GET /user/profile → UserProfileDto
    MockHandler(
        method = HttpMethod.Get,
        path = "/user/profile",
        resolver = { _, _ -> _currentUser }
    ),

    // PUT /user/profile — UpdateProfileDto : { firstName, lastName, email }
    MockHandler(
        method = HttpMethod.Put,
        path = "/user/profile",
        resolver = { _, body ->
            randomFailure()
            if (body != null) {
                runCatching {
                    val json = Json.parseToJsonElement(body).jsonObject
                    _currentUser = _currentUser.copy(
                        firstName = json["firstName"]?.jsonPrimitive?.content ?: _currentUser.firstName,
                        lastName  = json["lastName"]?.jsonPrimitive?.content  ?: _currentUser.lastName,
                        email     = json["email"]?.jsonPrimitive?.content     ?: _currentUser.email,
                    )
                }
            }
            _currentUser
        }
    ),

    // PUT /user/password — UpdatePasswordDto : { currentPassword, newPassword }
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

    // GET /user/subscriptions → SubscriptionDto[]  (status PascalCase "Active")
    MockHandler(
        method = HttpMethod.Get,
        path = "/user/subscriptions",
        resolver = { _, _ ->
            MockFactories.makeMany(3) { MockFactories.makeSubscription(status = "Active") }
        }
    ),

    // DELETE /user/subscriptions/:id
    MockHandler(
        method = HttpMethod.Delete,
        path = "/user/subscriptions/:id",
        status = HttpStatusCode.NoContent,
        resolver = { _, _ -> null }
    ),

    // GET /user/orders → OrderSummaryDto[]
    MockHandler(
        method = HttpMethod.Get,
        path = "/user/orders",
        resolver = { _, _ -> _accountOrders.toList() }
    ),

    // GET /user/orders/:id
    MockHandler(
        method = HttpMethod.Get,
        path = "/user/orders/:id",
        resolver = { params, _ -> _accountOrders.find { it.id.toString() == params["id"] } }
    ),
)

// ---------------------------------------------------------------------------
// Commandes — shape OrderSummaryDto (status PascalCase)
// ---------------------------------------------------------------------------

private val _accountOrders = mutableListOf(
    MockFactories.makeOrder("Paid"),
    MockFactories.makeOrder("Pending"),
    MockFactories.makeOrder("Failed"),
    MockFactories.makeOrder("Refunded"),
    MockFactories.makeOrder(),
    MockFactories.makeOrder(),
    MockFactories.makeOrder(),
    MockFactories.makeOrder(),
)