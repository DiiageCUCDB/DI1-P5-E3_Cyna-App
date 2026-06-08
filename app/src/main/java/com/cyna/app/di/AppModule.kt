package com.cyna.app.di

import com.cyna.app.BuildConfig
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.remote.*
import com.cyna.app.data.repository.*
import com.cyna.app.domain.repository.*
import com.cyna.app.mock.registry.buildMockEngine
import com.cyna.app.ui.screens.auth.AuthViewModel
import com.cyna.app.ui.screens.ordershistory.OrderHistoryViewModel
import com.cyna.app.ui.screens.profile.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext

private const val RMAPI_URL = " http://localhost:5104/"

val appModule = module {
    // ------------------------------------------------------------------
    // Local Managers
    // ------------------------------------------------------------------
    single { SessionManager(androidContext()) }

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
        val sessionManager = get<SessionManager>()
        createHttpClient(
            baseUrl = RMAPI_URL,
            engine  = get()
        ).config {
            install(Auth) {
                bearer {
                    loadTokens {
                        sessionManager.token.value?.let { BearerTokens(it, "") }
                    }
                }
            }
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
