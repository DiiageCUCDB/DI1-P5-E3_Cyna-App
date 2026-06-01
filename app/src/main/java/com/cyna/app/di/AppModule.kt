package com.cyna.app.di

import com.cyna.app.BuildConfig
import com.cyna.app.data.remote.*
import com.cyna.app.data.repository.*
import com.cyna.app.domain.repository.*
import com.cyna.app.mock.registry.buildMockEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

private const val RMAPI_URL = "http://98.66.234.231:8000/api/"

/**
 * Koin dependency injection module for the application.
 *
 * This module defines all the dependencies that will be available for injection
 * throughout the application. It configures how dependencies are created and
 * their lifecycle (singleton, factory, etc.).
* When [BuildConfig.MOCK_API] is `true` the Ktor client is backed by
 * [buildMockEngine], which intercepts every request and delegates to
 * [com.cyna.app.mock.registry.MockRegistry] — no network required.
 *
 * ## Usage
 * This module should be loaded when initializing the Koin container in the application.
 *
 * ## Dependencies Included
 * - [AuthRepository] as singleton using [AuthRepositoryImpl] implementation
 *
 * @see org.koin.dsl.module
 * @see single
 * @see org.koin.plugin.module.dsl.factory
 *
 * @sample
 * // Initialize Koin with this module
 * startKoin {
 *     modules(appModule)
 * }
* To enable mock mode add to your `local.properties`:
 * ```
 * MOCK_API=true
 * ```
 * and expose it in `build.gradle.kts`:
 * ```kotlin
 * buildConfigField("boolean", "MOCK_API",
 *     properties["MOCK_API"]?.toString() ?: "false")
 * ```
 */
val appModule = module {
    // ------------------------------------------------------------------
    // Engine — real (CIO) or mock, selected at compile-time flag
    // ------------------------------------------------------------------
    single<HttpClientEngine> {
        if (BuildConfig.MOCK_API) {
            buildMockEngine(delayMs = 400L)   // simulates ~400 ms latency
        } else {
            CIO.create()
        }
    }

    // ------------------------------------------------------------------
    // HTTP client — same factory, different engine
    // ------------------------------------------------------------------
    single<HttpClient> {
        createHttpClient(
            baseUrl = RMAPI_URL,
            engine  = get()
        )
    }

    // ------------------------------------------------------------------
    // API + Repository layer
    // ------------------------------------------------------------------
    // ── Remote API layer ──────────────────────────────────────────────────────
    single { AuthAPI(get()) }
    single { UserAPI(get()) }
    single { OrderHistoryAPI(get()) }

    // ── Repository layer ──────────────────────────────────────────────────────
    single<AuthRepository>         { AuthRepositoryImpl(get()) }
    single<UserRepository>   { UserRepositoryImpl(get()) }
    single<OrderHistoryRepository>  { OrderHistoryRepositoryImpl(get()) }


    // Add other dependencies here as needed
    // single { YourRepository() }
    // factory { YourUseCase() } // new instance each time
}