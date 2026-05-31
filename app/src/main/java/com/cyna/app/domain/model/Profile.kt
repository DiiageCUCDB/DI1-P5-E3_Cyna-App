package com.cyna.app.domain.model

data class Subscription(
    val id: String,
    val productName: String,
    val status: String,
    val duration: String,
    val quantity: Int,
    val unitPrice: Double,
    val endsAt: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isConfirmed: Boolean
)