package com.cyna.app.data.repository

import com.cyna.app.data.remote.UserAPI
import com.cyna.app.domain.model.Subscription
import com.cyna.app.domain.model.User
import com.cyna.app.domain.repository.UserRepository

internal class UserRepositoryImpl(
    private val userAPI: UserAPI
) : UserRepository {

    // UserProfileDto → User
    override suspend fun getMe(): User =
        userAPI.getMe().let { dto ->
            User(
                id             = dto.id,
                email          = dto.email,
                firstName      = dto.firstName,
                lastName       = dto.lastName,
                role           = dto.role,
                isEmailVerified = dto.isEmailVerified
            )
        }

    // UpdateProfileDto : { firstName, lastName, email }
    override suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String
    ): User = userAPI.updateProfile(firstName, lastName, email).let { dto ->
        User(
            id             = dto.id,
            email          = dto.email,
            firstName      = dto.firstName,
            lastName       = dto.lastName,
            role           = dto.role,
            isEmailVerified = dto.isEmailVerified
        )
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): String = userAPI.updatePassword(currentPassword, newPassword).message

    // SubscriptionDto → Subscription  (status PascalCase "Active")
    override suspend fun getSubscriptions(): List<Subscription> =
        userAPI.getSubscriptions()
            .filter { it.status == "Active" }
            .map { dto ->
                Subscription(
                    id                 = dto.id,
                    status             = dto.status,
                    productName        = dto.productName,
                    planName           = dto.planName,
                    currentPeriodStart = dto.currentPeriodStart,
                    currentPeriodEnd   = dto.currentPeriodEnd,
                    autoRenew          = dto.autoRenew
                )
            }

    override suspend fun cancelSubscription(id: String) {
        userAPI.cancelSubscription(id)
    }
}