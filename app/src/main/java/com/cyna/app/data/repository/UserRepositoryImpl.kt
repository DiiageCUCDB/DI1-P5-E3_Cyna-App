package com.cyna.app.data.repository

import com.cyna.app.data.remote.UserAPI
import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User
import com.cyna.app.domain.repository.UserRepository

internal class UserRepositoryImpl(
    private val userAPI: UserAPI
) : UserRepository {

    override suspend fun getMe(): User =
        userAPI.getMe().let { dto ->
            User(
                id          = dto.id,
                name        = dto.name,
                email       = dto.email,
                role        = dto.role,
                isConfirmed = dto.isConfirmed
            )
        }

    override suspend fun updateProfile(name: String, email: String): User =
        userAPI.updateProfile(name, email).let { dto ->
            User(
                id          = dto.id,
                name        = dto.name,
                email       = dto.email,
                role        = dto.role,
                isConfirmed = dto.isConfirmed
            )
        }

    override suspend fun updatePassword(currentPassword: String, newPassword: String): String =
        userAPI.updatePassword(currentPassword, newPassword).message

    override suspend fun getSubscriptions(): List<Subscription> =
        userAPI.getSubscriptions()
            .filter { it.status == "active" }
            .map { dto ->
                Subscription(
                    id          = dto.id,
                    productName = dto.productName,
                    status      = dto.status,
                    duration    = dto.duration,
                    quantity    = dto.quantity,
                    unitPrice   = dto.unitPrice,
                    endsAt      = dto.endsAt
                )
            }

    override suspend fun cancelSubscription(id: String) {
        userAPI.cancelSubscription(id)
    }
}