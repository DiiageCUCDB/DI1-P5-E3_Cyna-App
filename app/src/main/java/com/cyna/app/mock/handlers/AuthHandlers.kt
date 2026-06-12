package com.cyna.app.mock.handlers

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val authHandlers: List<MockHandler> = listOf(

    // POST /auth/login → { message }  (real API uses Set-Cookie for tokens)
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/login",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val email = json?.get("email")?.jsonPrimitive?.content ?: ""
            if (email == "error@example.com") error("Identifiants invalides.")
            MessageResponse("Connexion réussie.")
        }
    ),

    // POST /auth/register → { message }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/register",
        resolver = { _, _ -> MessageResponse("Inscription réussie.") }
    ),

    // POST /auth/refresh → { message }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/refresh",
        resolver = { _, _ -> MessageResponse("Rafraîchissement réussi.") }
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
