package com.cyna.app.data.remote

import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.SubscriptionDto
import com.cyna.app.data.dto.UserDto
import com.cyna.app.data.dto.UpdatePasswordRequest
import com.cyna.app.data.dto.UpdateProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.typeInfo

internal class UserAPI(private val client: HttpClient) {

    // GET /user/profile  →  UserProfileDto
    suspend fun getMe(): UserDto = client.get("user/profile")
        .accept(HttpStatusCode.OK)
        .body()

    // PUT /user/profile  →  UpdateProfileDto { firstName, lastName, email }
    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String
    ): UserDto = client.put("user/profile") {
        setBodyJson(UpdateProfileRequest(firstName, lastName, email))
    }.accept(HttpStatusCode.OK).body()

    // PUT /user/password  →  UpdatePasswordDto { currentPassword, newPassword }
    suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): MessageResponse = client.put("user/password") {
        setBodyJson(UpdatePasswordRequest(currentPassword, newPassword))
    }.accept(HttpStatusCode.OK).body()

    // GET /user/subscriptions  →  SubscriptionDto[]
    suspend fun getSubscriptions(): List<SubscriptionDto> =
        client.get("user/subscriptions")
            .accept(HttpStatusCode.OK)
            .body(typeInfo<List<SubscriptionDto>>())

    // DELETE /user/subscriptions/:id
    suspend fun cancelSubscription(id: String) =
        client.delete("user/subscriptions/$id")
            .accept(HttpStatusCode.NoContent, HttpStatusCode.OK)
}