package com.cyna.app.mock.registry

import io.ktor.http.*

// ---------------------------------------------------------------------------
// MockHandler — mirrors JS MockHandler typedef
// ---------------------------------------------------------------------------

/**
 * Represents a registered mock route.
 *
 * @param method    HTTP method (GET, POST, PUT, DELETE, PATCH)
 * @param path      Route pattern — supports :param segments e.g. "/products/:id"
 * @param status    HTTP status code returned (default 200)
 * @param resolver  Suspend function called with extracted path/query params and
 *                  the request body. Returns Any? which will be serialized to JSON.
 */
data class MockHandler(
    val method: HttpMethod,
    val path: String,
    val status: HttpStatusCode = HttpStatusCode.OK,
    val resolver: suspend (params: Map<String, String>, body: String?) -> Any?
)

// ---------------------------------------------------------------------------
// MockRegistry — singleton, mirrors registry.js
// ---------------------------------------------------------------------------

/**
 * Singleton registry that stores all mock route handlers.
 *
 * Usage:
 * ```kotlin
 * MockRegistry.register(
 *     MockHandler(HttpMethod.Get, "/products") { _, _ ->
 *         ProductFactories.makeMany(12) { makeProduct() }
 *     }
 * )
 * ```
 *
 * The [MockEngine] queries this registry on every intercepted request.
 */
object MockRegistry {

    private val handlers = mutableListOf<MockHandler>()

    // ------------------------------------------------------------------
    // Registration
    // ------------------------------------------------------------------

    fun register(handler: MockHandler): MockRegistry {
        handlers.add(handler)
        return this
    }

    fun registerMany(vararg newHandlers: MockHandler): MockRegistry {
        handlers.addAll(newHandlers)
        return this
    }

    fun registerMany(newHandlers: List<MockHandler>): MockRegistry {
        handlers.addAll(newHandlers)
        return this
    }

    fun clear(): MockRegistry {
        handlers.clear()
        return this
    }

    // ------------------------------------------------------------------
    // Resolution
    // ------------------------------------------------------------------

    /**
     * Find the first handler matching [method] and [path].
     *
     * Returns the handler plus the extracted path params, or null if none match.
     */
    fun resolve(method: HttpMethod, path: String): Pair<MockHandler, Map<String, String>>? {
        for (handler in handlers) {
            if (handler.method != method) continue
            val params = matchPattern(handler.path, path) ?: continue
            return handler to params
        }
        return null
    }

    // ------------------------------------------------------------------
    // Debug
    // ------------------------------------------------------------------

    fun listRoutes(): List<String> =
        handlers.map { "${it.method.value.padEnd(7)} ${it.path}" }

    // ------------------------------------------------------------------
    // Internal helpers — mirrors matchPattern() in client.js
    // ------------------------------------------------------------------

    /**
     * Match a concrete [url] against a [pattern] that may contain :param segments.
     * Returns extracted params or null if no match.
     *
     * e.g. matchPattern("/products/:id", "/products/abc") → mapOf("id" to "abc")
     */
    private fun matchPattern(pattern: String, url: String): Map<String, String>? {
        // Strip query string from url before matching
        val cleanUrl = url.substringBefore("?")

        val patternParts = pattern.split("/")
        val urlParts = cleanUrl.split("/")

        if (patternParts.size != urlParts.size) return null

        val extracted = mutableMapOf<String, String>()
        for (i in patternParts.indices) {
            val p = patternParts[i]
            val u = urlParts[i]
            when {
                p.startsWith(":") -> extracted[p.drop(1)] = u
                p != u -> return null
            }
        }
        return extracted
    }
}