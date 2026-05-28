package com.cyna.app

import android.app.Application
import com.cyna.app.di.appModule
import com.cyna.app.mock.MockInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        // ------------------------------------------------------------------
        // 1. Register mock handlers BEFORE Koin builds the HttpClient.
        //    This mirrors `await import("./mocks/index.js")` in main.tsx.
        //    In release builds MOCK_API=false so this block is dead code
        //    and can be stripped by R8/ProGuard.
        // ------------------------------------------------------------------
        if (BuildConfig.MOCK_API) {
            MockInitializer.init(debug = BuildConfig.DEBUG)
        }

        // ------------------------------------------------------------------
        // 2. Start Koin — AppModule reads BuildConfig.MOCK_API to choose
        //    the right Ktor engine (real CIO vs MockEngine).
        // ------------------------------------------------------------------
        startKoin {
            // Log Koin events (optional - use Level.NONE for production)
            androidLogger(level = Level.ERROR)

            // Inject Android context
            androidContext(this@Application)

            // Load modules
            modules(appModule)
        }
    }
}