package com.cyna.app.data.remote

import com.cyna.app.data.dto.AuthResponse
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RefreshTokenRequest
import com.cyna.app.data.dto.RegisterRequest
import com.cyna.app.data.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AuthAPI(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()
    }

    suspend fun refresh(request: RefreshTokenRequest): AuthResponse {
        return client.post("auth/refresh") {
            contentType(ContentType.Application.Json)
            setBodyJson(request)
        }.body()
    }

    suspend fun logout(refreshToken: String) {
        client.post("auth/logout") {
            contentType(ContentType.Application.Json)
            setBodyJson(RefreshTokenRequest(refreshToken))
        }
    }

    suspend fun getCurrentUser(): UserDto {
        return client.get("auth/me").body()
    }
}
