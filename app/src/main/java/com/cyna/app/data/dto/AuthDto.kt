package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

/** Corps de la requête `POST /auth/login`. */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/** Corps de la requête `POST /auth/register`. */
@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

/** Corps de la requête `POST /auth/logout`. */
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Réponse portant les tokens JWT (non utilisée pour login/register qui passent par cookies).
 * Conservée pour compatibilité avec d'éventuels endpoints futurs.
 */
@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String
)

/** Profil utilisateur renvoyé par `GET /auth/me`. Persisté dans [com.cyna.app.data.local.SessionManager]. */
@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isEmailVerified: Boolean = false
)