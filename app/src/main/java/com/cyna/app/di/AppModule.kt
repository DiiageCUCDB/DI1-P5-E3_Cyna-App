package com.cyna.app.di

import com.cyna.app.BuildConfig
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.remote.*
import com.cyna.app.data.repository.*
import com.cyna.app.data.util.*
import com.cyna.app.domain.repository.*
import com.cyna.app.mock.registry.buildMockEngine
import com.cyna.app.ui.screens.auth.AuthViewModel
import com.cyna.app.ui.screens.ordershistory.OrderHistoryViewModel
import com.cyna.app.ui.screens.profile.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val RMAPI_URL = "https://10.0.2.2:7169/"

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
    // Local Managers
    // ------------------------------------------------------------------
    single { SessionManager(androidContext()) }

    // ------------------------------------------------------------------
    // Utilitaires Android
    // ------------------------------------------------------------------
    single { VibrationHelper(androidContext()) }

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
    // HTTP client
    // ------------------------------------------------------------------
    single<HttpClient> {
        HttpClient(get()) {
            // Configuration globale (sérialisation JSON)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            // Gestion des cookies HttpOnly par Ktor
            install(HttpCookies)
        }
    }

    // ------------------------------------------------------------------
    // API + Repository layer
    // ------------------------------------------------------------------
    single { AuthAPI(get()) }
    single { UserAPI(get()) }
    single { OrderHistoryAPI(get()) }

    single<AuthRepository>         { AuthRepositoryImpl(get(), get()) }
    single<UserRepository>   { UserRepositoryImpl(get()) }
    single<OrderHistoryRepository>  { OrderHistoryRepositoryImpl(get()) }

    // ------------------------------------------------------------------
    // ViewModels
    // ------------------------------------------------------------------
    viewModel { AuthViewModel(get(), get()) }
    viewModel { ProfileViewModel(androidApplication()) }
    viewModel { OrderHistoryViewModel(androidApplication()) }
}
