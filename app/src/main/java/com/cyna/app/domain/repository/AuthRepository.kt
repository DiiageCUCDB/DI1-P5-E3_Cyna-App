package com.cyna.app.domain.repository

interface AuthRepository {
    /**
     * Logs out the current user.
     * Implementations should treat any server-side error as non-fatal —
     * the session is considered ended regardless.
     */
    suspend fun logout()
}