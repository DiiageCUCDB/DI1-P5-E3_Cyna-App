package com.cyna.app.data.repository

import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.remote.AuthAPI
import com.cyna.app.data.remote.UserAPI
import com.cyna.app.domain.repository.AuthRepository

/**
 * Implémentation de [AuthRepository] qui orchestre les appels à [AuthAPI] et [UserAPI]
 * et gère la session locale via [SessionManager].
 */
internal class AuthRepositoryImpl(
    private val authAPI: AuthAPI,
    private val userAPI: UserAPI,
    private val sessionManager: SessionManager
) : AuthRepository {

    /**
     * Connecte l'utilisateur et charge son profil.
     *
     * En mode production, les cookies `cyna_token` / `cyna_refresh_token` sont stockés
     * automatiquement par [SessionManagerCookieStorage] pendant la réponse.
     * En mode mock (pas de `Set-Cookie`), un token fictif est injecté pour que la navigation
     * fonctionne sans modifier la logique d'authentification.
     */
    override suspend fun login(request: LoginRequest): MessageResponse {
        val response = authAPI.login(request)
        // cyna_token and cyna_refresh_token cookies are stored automatically by
        // SessionManagerCookieStorage (via the HttpCookies plugin) during the response pipeline.
        // In mock mode (no Set-Cookie headers), we fall back to a fake token so navigation works.
        if (sessionManager.token.value.isNullOrEmpty()) {
            sessionManager.saveTokens("mock-session-token", "mock-refresh-token")
        }
        runCatching { userAPI.getMe() }.getOrNull()?.let { sessionManager.saveUser(it) }
        return response
    }

    override suspend fun register(request: RegisterRequest): MessageResponse {
        return authAPI.register(request)
    }

    /** Invalide la session serveur puis efface les tokens locaux (dans `finally` pour garantir le nettoyage). */
    override suspend fun logout() {
        val refreshToken = sessionManager.refreshToken.value
        try {
            if (refreshToken != null) authAPI.logout(refreshToken)
        } finally {
            sessionManager.clearSession()
        }
    }
}
