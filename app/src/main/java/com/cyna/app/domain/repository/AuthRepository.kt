package com.cyna.app.domain.repository

import com.cyna.app.data.dto.AuthResponse
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
    /**
     * Logs out the current user.
     * Implementations should treat any server-side error as non-fatal —
     * the session is considered ended regardless.
     */
    suspend fun logout()
}
