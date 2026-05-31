package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateProfileRequest(
    val name: String,
    val email: String
)

@Serializable
internal data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
internal data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val isConfirmed: Boolean,
    val is2faEnabled: Boolean,
    val createdAt: String
)

@Serializable
internal data class SubscriptionDto(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val status: String,
    val duration: String,
    val quantity: Int,
    val unitPrice: Double,
    val startsAt: String,
    val endsAt: String,
    val createdAt: String
)