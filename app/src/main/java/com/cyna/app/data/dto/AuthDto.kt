package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val user: UserDto,
    val token: String
)
