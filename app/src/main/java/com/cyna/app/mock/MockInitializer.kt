package com.cyna.app.mock

import android.util.Log
import com.cyna.app.mock.handlers.*
import com.cyna.app.mock.registry.MockRegistry

/**
 * Registers all mock handlers into [MockRegistry].
 *
 * Call this **once** during app startup — before any Ktor request is made.
 * In production builds this object should never be referenced.
 *
 * Mirrors `mocks/index.js`.
 *
 * Usage in [Application.onCreate]:
 * ```kotlin
 * if (BuildConfig.MOCK_API) {
 *     MockInitializer.init(debug = BuildConfig.DEBUG)
 * }
 * ```
 */
object MockInitializer {

    private const val TAG = "MockRegistry"

    fun init(debug: Boolean = false) {
        MockRegistry.clear()

        MockRegistry.registerMany(
                authHandlers
                + userHandlers
                + subscriptionHandlers
                + accountOrderHandlers
                + serviceHandlers
        )

        if (debug) {
            Log.d(TAG, "=== Registered mock routes ===")
            MockRegistry.listRoutes().forEach { Log.d(TAG, it) }
        }
    }
}