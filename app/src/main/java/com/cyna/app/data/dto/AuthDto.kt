package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)