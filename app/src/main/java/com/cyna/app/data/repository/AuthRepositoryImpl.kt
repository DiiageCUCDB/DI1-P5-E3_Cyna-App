package com.cyna.app.data.repository

import com.cyna.app.data.dto.AuthResponse
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.remote.AuthAPI
import com.cyna.app.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val authAPI: AuthAPI,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): AuthResponse {
        val response = authAPI.login(request)
        sessionManager.saveSession(response.user, response.token)
        return response
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        val response = authAPI.register(request)
        sessionManager.saveSession(response.user, response.token)
        return response
    }

    /**
     * Calls POST /auth/logout.
     * [io.ktor.client.plugins.HttpCallValidator] fires the error toast before
     * the exception surfaces here. On failure the exception propagates so the
     * caller can decide not to end the session.
     */
    override suspend fun logout() {
        try {
            authAPI.logout()
        } finally {
            sessionManager.clearSession()
        }
    }
}
