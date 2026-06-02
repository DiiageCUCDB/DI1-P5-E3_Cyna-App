// ============================================================
// build.gradle.kts — relevant additions only
// ============================================================
//
// 1. Read MOCK_API from local.properties (never commit this file).
//    Create/edit android/local.properties:
//
//      MOCK_API=true        ← enable mock mode
//      MOCK_API=false       ← real network (default)
//
// 2. The flag is exposed as BuildConfig.MOCK_API (Boolean).
//    It is evaluated at compile time, so R8 will eliminate all
//    mock code from release builds when MOCK_API=false.
// ============================================================

import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Read local.properties safely
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

android {
    namespace = "com.cyna.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cyna.app"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose MOCK_API flag — defaults to false in production
        buildConfigField(
            "boolean",
            "MOCK_API",
            localProps.getProperty("MOCK_API", "false")
        )
    }

    buildTypes {
        debug {
            // Optionally force mock on in debug without local.properties
            // buildConfigField("boolean", "MOCK_API", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Always false in release — mock code stripped by R8
            buildConfigField("boolean", "MOCK_API", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig  = true   // ← required for BuildConfig.MOCK_API
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Mock engine — Ktor's built-in test/mock engine
    implementation(libs.ktor.client.mock)

    implementation(libs.kotlinx.serialization.json)

    // Fix for Kindling - exclude Compose dependencies that cause conflicts
    implementation(libs.kindling.core)
    implementation(libs.kindling.compose)
    implementation(libs.kindling.utils)

    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
