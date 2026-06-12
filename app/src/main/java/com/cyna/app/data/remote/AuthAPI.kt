package com.cyna.app.data.remote

import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.RefreshTokenRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Passerelle vers les endpoints `/auth/*` de l'API Cyna.
 *
 * L'authentification est basée sur des cookies HTTP (`cyna_token`, `cyna_refresh_token`)
 * renvoyés par l'API via `Set-Cookie`. Les corps de réponse de login/register ne contiennent
 * qu'un [MessageResponse] — les tokens sont injectés automatiquement dans le stockage de
 * cookies par [SessionManagerCookieStorage].
 */
internal class AuthAPI(private val client: HttpClient) {

    /** Authentifie l'utilisateur. Les cookies de session sont définis par la réponse `Set-Cookie`. */
    suspend fun login(request: LoginRequest): MessageResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()
    }

    /** Crée un nouveau compte. Redirige l'utilisateur vers la page de connexion après succès. */
    suspend fun register(request: RegisterRequest): MessageResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()
    }

    /** Invalide la session côté serveur. [SessionManager.clearSession] est appelé dans le repository. */
    suspend fun logout(refreshToken: String) {
        client.post("auth/logout") {
            contentType(ContentType.Application.Json)
            setBodyJson(RefreshTokenRequest(refreshToken))
        }
    }

    /** Récupère le profil de l'utilisateur connecté via le cookie de session. */
    suspend fun getCurrentUser(): UserDto {
        return client.get("auth/me").body()
    }
}
