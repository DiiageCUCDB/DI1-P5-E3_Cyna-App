package com.cyna.app.data.remote

import com.cyna.app.data.dto.ErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import dev.kindling.core.components.KToastManager
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText

fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create()
): HttpClient = HttpClient(engine) {
    defaultRequest {
        url(baseUrl)
    }

    install(ContentNegotiation) {
        json()
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 15000
        socketTimeoutMillis = 15000
        requestTimeoutMillis = 15000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("HTTP Client: $message")
            }
        }
        level = LogLevel.ALL
    }

    install(HttpCallValidator) {
        validateResponse { response ->
            val status = response.status.value
            when {
                status == 200 -> Unit // OK, do nothing

                status in 400..499 -> {
                    val msg = runCatching { response.body<ErrorResponse>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    KToastManager.warning("Client error ($status)", msg)
                    throw HttpException.ClientError(status, msg)
                }

                status in 500..599 -> {
                    val msg = runCatching { response.body<ErrorResponse>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    KToastManager.error("Server error ($status)", msg)
                    throw HttpException.ServerError(status, msg)
                }
            }
        }

        handleResponseExceptionWithRequest { exception, _ ->
            if (exception is HttpException) return@handleResponseExceptionWithRequest // already handled above
            KToastManager.error("Network error", exception.message ?: "No details provided")
        }
    }
}

inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

fun HttpResponse.accept(vararg codes: HttpStatusCode) = apply {
    println("Checking response status: $status, accepted codes: ${codes.joinToString()}")
    if (status !in codes) {
        val message = "Unexpected status: HTTP $status"
        val description = "Expected: ${codes.joinToString()}"
        KToastManager.warning(message, description)
        throw HttpException.NotAccepted("$message. $description")
    }
}

sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted(message: String) : HttpException(message)
    class ClientError(val statusCode: Int, message: String) : HttpException(message)
    class ServerError(val statusCode: Int, message: String) : HttpException(message)
}