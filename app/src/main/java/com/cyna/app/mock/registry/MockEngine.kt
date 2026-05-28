package com.cyna.app.mock.registry

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Builds a Ktor [MockEngine] backed by [MockRegistry].
 *
 * Every outgoing request is intercepted:
 * 1. The path is matched against registered handlers.
 * 2. Query parameters are extracted and merged into the params map.
 * 3. The resolver is called and its result is serialised to JSON.
 * 4. If no handler matches, a 404 is returned.
 *
 * Usage (in AppModule):
 * ```kotlin
 * val engine = if (BuildConfig.MOCK_API) buildMockEngine() else CIO.create()
 * single<HttpClient> { createHttpClient(baseUrl = BASE_URL, engine = engine) }
 * ```
 */
fun buildMockEngine(delayMs: Long = 400L): HttpClientEngine = MockEngine { request ->
    // Simulate network latency
    kotlinx.coroutines.delay(delayMs)

    val method = request.method
    val fullPath = request.url.encodedPath          // e.g. /api/products/abc
    // Strip the API prefix so handlers use clean paths like /products/:id
    val path = fullPath.removePrefix("/api")

    // Extract query params
    val queryParams: Map<String, String> = request.url.parameters.entries()
        .associate { (key, values) -> key to (values.firstOrNull() ?: "") }

    // Read body
    val body: String? = runCatching {
        (request.body as? OutgoingContent.ByteArrayContent)
            ?.bytes()
            ?.decodeToString()
    }.getOrNull()

    val match = MockRegistry.resolve(method, path)

    if (match == null) {
        respond(
            content = """{"error":"No mock handler registered for ${method.value} $path"}""",
            status = HttpStatusCode.NotFound,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    } else {
        val (handler, pathParams) = match
        // Merge path params + query params (path params win on collision)
        val mergedParams = queryParams + pathParams

        val result = runCatching { handler.resolver(mergedParams, body) }

        result.fold(
            onSuccess = { data ->
                val json = when (data) {
                    null -> "null"
                    is String -> data                         // already raw JSON
                    is Unit -> "{}"
                    else -> json.encodeToString(
                        json.serializersModule.serializer(data::class.java),
                        @Suppress("UNCHECKED_CAST")
                        data as Any
                    )
                }
                respond(
                    content = json,
                    status = handler.status,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            },
            onFailure = { ex ->
                respond(
                    content = """{"error":"${ex.message?.replace("\"", "\\\"")}"}""",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
        )
    }
}

// Shared Json instance for serialisation inside the engine
private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}