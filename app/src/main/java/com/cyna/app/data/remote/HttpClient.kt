package com.cyna.app.data.remote

import com.cyna.app.data.dto.ErrorResponse
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.util.VibrationHelper
import dev.kindling.core.components.KToastManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Crée et configure le client HTTP Ktor partagé par toutes les API.
 *
 * Comportement :
 * - Ajoute [baseUrl] comme préfixe de toutes les requêtes.
 * - Installe [HttpCookies] uniquement si [sessionManager] est fourni — les cookies
 *   `cyna_token` / `cyna_refresh_token` sont alors lus/écrits via [SessionManagerCookieStorage].
 * - Différencie les 401 sur `/auth/login` ou `/auth/register` (identifiants invalides)
 *   des 401 sur les routes protégées (session expirée → [SessionManager.clearSession]).
 *
 * @param baseUrl URL de base de l'API (ex. `"https://api.exemple.com/"`).
 * @param engine Moteur HTTP à utiliser. Défaut : CIO (production). Injecter OkHttp en debug.
 * @param vibrationHelper Retour haptique sur erreur réseau. `null` = pas de vibration.
 * @param sessionManager Gestionnaire de session pour l'auth par cookie. `null` = client non authentifié.
 */
fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create(),
    vibrationHelper: VibrationHelper? = null,
    sessionManager: SessionManager? = null
): HttpClient = HttpClient(engine) {

    defaultRequest { url(baseUrl) }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        socketTimeoutMillis  = 15_000
        requestTimeoutMillis = 15_000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) { println("HTTP Client: $message") }
        }
        level = LogLevel.ALL
    }

    install(HttpCallValidator) {
        validateResponse { response ->
            val status = response.status.value
            when {
                status in 200..299 -> Unit

                status == 401 -> {
                    val path = response.call.request.url.encodedPath
                    val isAuthEndpoint = path.endsWith("/auth/login") ||
                        path.endsWith("/auth/register")
                    if (isAuthEndpoint) {
                        // Wrong credentials — show API message, don't touch session
                        val msg = runCatching { response.body<ErrorResponse>().text }
                            .recoverCatching { response.bodyAsText().take(200) }
                            .getOrDefault("Identifiants invalides.")
                        vibrationHelper?.warning()
                        KToastManager.warning("Connexion échouée", msg)
                        throw HttpException.ClientError(status, msg)
                    } else {
                        // Expired session on a protected endpoint
                        sessionManager?.clearSession()
                        vibrationHelper?.warning()
                        KToastManager.warning("Session expirée", "Veuillez vous reconnecter.")
                        throw HttpException.ClientError(status, "Session expirée")
                    }
                }

                status in 400..499 -> {
                    val msg = runCatching { response.body<ErrorResponse>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    vibrationHelper?.warning()
                    KToastManager.warning("Client error ($status)", msg)
                    throw HttpException.ClientError(status, msg)
                }

                status in 500..599 -> {
                    val msg = runCatching { response.body<ErrorResponse>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    vibrationHelper?.error()
                    KToastManager.error("Server error ($status)", msg)
                    throw HttpException.ServerError(status, msg)
                }
            }
        }

        handleResponseExceptionWithRequest { exception, _ ->
            if (exception is HttpException) return@handleResponseExceptionWithRequest
            vibrationHelper?.error()
            KToastManager.error("Network error", exception.message ?: "No details provided")
        }
    }

    // Cookie-based auth: cyna_token / cyna_refresh_token are stored in SessionManager
    // and sent automatically on every request.
    if (sessionManager != null) {
        install(HttpCookies) {
            storage = SessionManagerCookieStorage(sessionManager)
        }
    }
}

// ── Cookie storage backed by SessionManager (persists across app restarts) ───

/**
 * Implémentation de [CookiesStorage] qui persiste les cookies d'authentification
 * dans [SessionManager] (SharedPreferences) plutôt qu'en mémoire.
 *
 * Cookies gérés : `cyna_token` et `cyna_refresh_token`.
 * Le stockage est mis à jour à chaque `Set-Cookie` renvoyé par l'API (login, refresh).
 */
private class SessionManagerCookieStorage(
    private val sessionManager: SessionManager
) : CookiesStorage {

    override suspend fun get(requestUrl: Url): List<Cookie> = buildList {
        sessionManager.token.value?.takeIf { it.isNotEmpty() }
            ?.let { add(Cookie(name = "cyna_token", value = it)) }
        sessionManager.refreshToken.value?.takeIf { it.isNotEmpty() }
            ?.let { add(Cookie(name = "cyna_refresh_token", value = it)) }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        when (cookie.name) {
            "cyna_token" -> sessionManager.saveTokens(
                cookie.value,
                sessionManager.refreshToken.value ?: ""
            )
            "cyna_refresh_token" -> sessionManager.saveTokens(
                sessionManager.token.value ?: "",
                cookie.value
            )
        }
    }

    override fun close() {}
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/** Sérialise [body] en JSON et applique `Content-Type: application/json`. */
inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

/** Vérifie que le statut HTTP est parmi [codes] ; lève [HttpException.NotAccepted] sinon. */
fun HttpResponse.accept(vararg codes: HttpStatusCode) = apply {
    if (status !in codes) {
        val message     = "Unexpected status: HTTP $status"
        val description = "Expected: ${codes.joinToString()}"
        KToastManager.warning(message, description)
        throw HttpException.NotAccepted("$message. $description")
    }
}

// ── Exceptions ────────────────────────────────────────────────────────────────

/**
 * Hiérarchie d'exceptions levées par le client HTTP.
 *
 * - [NotAccepted] : statut HTTP reçu non attendu (via [accept]).
 * - [ClientError] : réponse 4xx (ex. 400, 401, 403, 404).
 * - [ServerError] : réponse 5xx.
 */
sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted(message: String)                         : HttpException(message)
    class ClientError(val statusCode: Int, message: String)    : HttpException(message)
    class ServerError(val statusCode: Int, message: String)    : HttpException(message)
}
