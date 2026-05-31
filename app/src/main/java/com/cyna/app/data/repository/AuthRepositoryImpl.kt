package com.cyna.app.data.repository

import com.cyna.app.data.remote.AuthAPI
import com.cyna.app.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val authAPI: AuthAPI
) : AuthRepository {

    /**
     * Calls POST /auth/logout.
     * [io.ktor.client.plugins.HttpCallValidator] fires the error toast before
     * the exception surfaces here. On failure the exception propagates so the
     * caller can decide not to end the session.
     */
    override suspend fun logout() {
        authAPI.logout()
    }
}