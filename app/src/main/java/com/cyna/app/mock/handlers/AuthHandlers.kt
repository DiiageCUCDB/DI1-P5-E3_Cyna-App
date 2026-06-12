package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val authHandlers: List<MockHandler> = listOf(

    // POST /auth/login → { token, refreshToken }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/login",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val email = json?.get("email")?.jsonPrimitive?.content ?: ""
            if (email == "error@example.com") error("Identifiants invalides.")
            MockFactories.makeAuthResponse()
        }
    ),

    // POST /auth/register → { token, refreshToken }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/register",
        resolver = { _, _ -> MockFactories.makeAuthResponse() }
    ),

    // POST /auth/refresh → { token, refreshToken }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/refresh",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val refreshToken = json?.get("refreshToken")?.jsonPrimitive?.content
            if (refreshToken.isNullOrBlank()) error("Refresh token manquant.")
            MockFactories.makeAuthResponse()
        }
    ),

    // POST /auth/logout
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/logout",
        resolver = { _, _ ->
            if (Math.random() < 0.25) error("Erreur lors de la déconnexion côté serveur.")
            MessageResponse("Déconnecté avec succès.")
        }
    ),

    // GET /auth/me → UserDto
    MockHandler(
        method = HttpMethod.Get,
        path = "/auth/me",
        resolver = { _, _ -> MockFactories.makeDemoUser() }
    ),

    // POST /auth/reset-password
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/reset-password",
        resolver = { _, _ -> MessageResponse("Mot de passe réinitialisé avec succès.") }
    ),
)
