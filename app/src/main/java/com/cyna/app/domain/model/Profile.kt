package com.cyna.app.domain.model

// ---------------------------------------------------------------------------
// User  ←  UserProfileDto
// ---------------------------------------------------------------------------

data class User(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isEmailVerified: Boolean
) {
    /** Nom complet calculé — remplace l'ancien champ `name`. */
    val fullName: String get() = "$firstName $lastName".trim()

    /** Initiales (ex: "JD") pour l'avatar. */
    val initials: String get() =
        listOfNotNull(firstName.firstOrNull(), lastName.firstOrNull())
            .joinToString("") { it.uppercase() }
            .ifEmpty { "?" }
}

// ---------------------------------------------------------------------------
// Subscription  ←  SubscriptionDto
// ---------------------------------------------------------------------------

data class Subscription(
    val id: Int,
    val status: String,
    val productName: String,
    val planName: String,
    val currentPeriodStart: String,
    val currentPeriodEnd: String,
    val autoRenew: Boolean
)