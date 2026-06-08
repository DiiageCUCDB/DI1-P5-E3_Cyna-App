package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ---------------------------------------------------------------------------
// Auth handlers — miroir de handlers/auth.js
// ---------------------------------------------------------------------------

val authHandlers: List<MockHandler> = listOf(

    // POST /auth/login
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/login",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val email = json?.get("email")?.jsonPrimitive?.content ?: ""
            val password = json?.get("password")?.jsonPrimitive?.content ?: ""

            if (email == "error@example.com") {
                error("Identifiants invalides.")
            }
            MockFactories.makeAuthResponse(MockFactories.makeUser(email = email))
        }
    ),

    // POST /auth/register
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/register",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val email = json?.get("email")?.jsonPrimitive?.content ?: ""
            val fullName = json?.get("fullName")?.jsonPrimitive?.content ?: ""
            val password = json?.get("password")?.jsonPrimitive?.content ?: ""

            MockFactories.makeAuthResponse(MockFactories.makeUser(email = email, firstName = fullName))
        }
    ),

    // POST /auth/logout
    // The client deletes its token regardless — handler may fail randomly
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/logout",
        resolver = { _, _ ->
            if (Math.random() < 0.25) error("Erreur lors de la déconnexion côté serveur.")
            MessageResponse("Déconnecté avec succès.")
        }
    ),

    // POST /auth/reset-password
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/reset-password",
        resolver = { _, _ -> MessageResponse("Mot de passe réinitialisé avec succès.") }
    ),
)