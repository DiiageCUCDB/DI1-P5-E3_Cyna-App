package com.cyna.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode

internal class AuthAPI(private val client: HttpClient) {

    /**
     * Throws [HttpException] on server/network failure —
     * [io.ktor.client.plugins.HttpCallValidator] will have already shown the toast before this throws.
     */
    suspend fun logout() {
        client.post("auth/logout")
            .accept(HttpStatusCode.OK, HttpStatusCode.NoContent)
    }
}