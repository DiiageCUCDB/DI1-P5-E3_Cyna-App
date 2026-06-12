package com.cyna.app.di

import com.cyna.app.BuildConfig
import com.cyna.app.data.local.SessionManager
import com.cyna.app.data.remote.*
import com.cyna.app.data.repository.*
import com.cyna.app.data.util.*
import com.cyna.app.domain.repository.*
import com.cyna.app.mock.registry.buildMockEngine
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { SessionManager(androidContext()) }

    single { VibrationHelper(androidContext()) }

    single<HttpClientEngine> {
        if (BuildConfig.MOCK_API) buildMockEngine(delayMs = 400L) else CIO.create()
    }

    single<HttpClient> {
        createHttpClient(
            baseUrl = BuildConfig.BASE_URL,
            engine = get(),
            vibrationHelper = get(),
            sessionManager = get()
        )
    }

    single { AuthAPI(get()) }
    single { UserAPI(get()) }
    single { OrderHistoryAPI(get()) }

    single<AuthRepository>        { AuthRepositoryImpl(get(), get()) }
    single<UserRepository>        { UserRepositoryImpl(get()) }
    single<OrderHistoryRepository> { OrderHistoryRepositoryImpl(get()) }
}
