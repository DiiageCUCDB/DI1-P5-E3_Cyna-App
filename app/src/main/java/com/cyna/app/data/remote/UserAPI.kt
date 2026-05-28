package com.cyna.app.data.remote

import com.cyna.app.data.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

internal class UserAPI(private val client: HttpClient) {

    suspend fun getMe(): UserDto = client.get("auth/me")
        .accept(HttpStatusCode.OK)
        .body()

    suspend fun updateProfile(name: String, email: String): UserDto =
        client.put("user/profile") {
            setBodyJson(UpdateProfileRequest(name, email))
        }.accept(HttpStatusCode.OK).body()

    suspend fun updatePassword(currentPassword: String, newPassword: String): MessageResponse =
        client.put("user/password") {
            setBodyJson(UpdatePasswordRequest(currentPassword, newPassword))
        }.accept(HttpStatusCode.OK).body()

    suspend fun getSubscriptions(): List<SubscriptionDto> =
        client.get("user/subscriptions")
            .accept(HttpStatusCode.OK)
            .body()

    suspend fun cancelSubscription(id: String) =
        client.delete("subscriptions/$id")
            .accept(HttpStatusCode.NoContent, HttpStatusCode.OK)
}