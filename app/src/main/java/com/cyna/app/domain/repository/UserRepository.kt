package com.cyna.app.domain.repository

import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User

interface UserRepository {
    suspend fun getMe(): User
    suspend fun updateProfile(name: String, email: String): User
    suspend fun updatePassword(currentPassword: String, newPassword: String): String
    suspend fun getSubscriptions(): List<Subscription>
    suspend fun cancelSubscription(id: String)
}