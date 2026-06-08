package com.cyna.app.data.remote

import com.cyna.app.data.dto.AuthResponse
import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

internal class AuthAPI(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Throws [HttpException] on server/network failure —
     * [io.ktor.client.plugins.HttpCallValidator] will have already shown the toast before this throws.
     */
    suspend fun logout() {
        client.post("auth/logout")
            .accept(HttpStatusCode.OK, HttpStatusCode.NoContent)
    }
}
