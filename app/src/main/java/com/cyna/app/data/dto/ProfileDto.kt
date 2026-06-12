package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

// ---------------------------------------------------------------------------
// UpdateProfileDto (PUT /user/profile body)
// ---------------------------------------------------------------------------

@Serializable
internal data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)

@Serializable
data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

// ---------------------------------------------------------------------------
// UserProfileDto (GET /user/profile response)
// { id, email, firstName, lastName, role, isEmailVerified, createdAt }
// ---------------------------------------------------------------------------

@Serializable
data class UserProfileDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isEmailVerified: Boolean,
    val createdAt: String
)

// ---------------------------------------------------------------------------
// SubscriptionDto (GET /user/subscriptions items)
// { id, status, productName, planName,
//   currentPeriodStart, currentPeriodEnd, autoRenew }
// ---------------------------------------------------------------------------

@Serializable
internal data class SubscriptionDto(
    val id: Int,
    val status: String,
    val productName: String,
    val planName: String,
    val currentPeriodStart: String,
    val currentPeriodEnd: String,
    val autoRenew: Boolean
)