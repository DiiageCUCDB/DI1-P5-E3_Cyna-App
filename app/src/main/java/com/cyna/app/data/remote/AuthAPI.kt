package com.cyna.app.data.remote

import com.cyna.app.data.dto.AuthResponse
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AuthAPI(private val client: HttpClient) {

    /**
     * Appelle le endpoint de connexion pour authentifier un compte utilisateur
     */
    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Appelle le endpoint d'inscription pour créer un compte utilisateur
     */
    suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Appelle le endpoint de déconnexion pour détruire les cookies côté serveur.
     */
    suspend fun logout() {
        client.post("auth/logout")
    }

    /**
     * Récupère les informations de l'utilisateur actuellement connecté.
     */
    suspend fun getCurrentUser(): UserDto {
        return client.get("auth/me").body()
    }
}
