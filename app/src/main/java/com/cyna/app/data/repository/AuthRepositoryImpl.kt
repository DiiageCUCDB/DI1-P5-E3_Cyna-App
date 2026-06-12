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
        // Save tokens first so the Auth plugin can inject them for the getCurrentUser call.
        sessionManager.saveTokens(response.token, response.refreshToken)
        val user = authAPI.getCurrentUser()
        sessionManager.saveUser(user)
        return response
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        val response = authAPI.register(request)
        sessionManager.saveTokens(response.token, response.refreshToken)
        val user = authAPI.getCurrentUser()
        sessionManager.saveUser(user)
        return response
    }

    override suspend fun logout() {
        val refreshToken = sessionManager.refreshToken.value
        try {
            if (refreshToken != null) authAPI.logout(refreshToken)
        } finally {
            sessionManager.clearSession()
        }
    }
}
