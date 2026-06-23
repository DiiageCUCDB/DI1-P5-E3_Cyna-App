# Structure du projet et conventions

## Arborescence

```
Cyna-App/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/cyna/app/
│           │
│           ├── Application.kt              ← Initialise MockInitializer + Koin
│           ├── MainActivity.kt             ← Hôte Compose, AppTheme, Toaster
│           │
│           ├── data/
│           │   ├── dto/                    ← @Serializable — forme exacte de l'API
│           │   │   ├── ApiDto.kt           ← ErrorResponse, MessageResponse
│           │   │   ├── AuthDto.kt          ← LoginRequest, UserDto, …
│           │   │   ├── ProfileDto.kt       ← UserProfileDto, SubscriptionDto, …
│           │   │   └── AccountOrderDto.kt  ← OrderItemDto, AccountOrderDto
│           │   ├── local/
│           │   │   └── SessionManager.kt   ← Tokens JWT + profil (SharedPreferences)
│           │   ├── remote/
│           │   │   ├── HttpClient.kt       ← createHttpClient(), HttpException, cookies
│           │   │   ├── AuthAPI.kt
│           │   │   ├── UserAPI.kt
│           │   │   ├── ServiceAPI.kt
│           │   │   └── OrderHistoryAPI.kt
│           │   ├── repository/
│           │   │   ├── AuthRepositoryImpl.kt
│           │   │   ├── UserRepositoryImpl.kt
│           │   │   ├── ServiceRepositoryimpl.kt
│           │   │   └── OrderHistoryRepositoryImpl.kt
│           │   └── util/
│           │       └── VibrationHelper.kt
│           │
│           ├── di/
│           │   └── AppModule.kt            ← Toutes les déclarations Koin
│           │
│           ├── domain/
│           │   ├── model/                  ← Data classes pures (aucune dépendance Android)
│           │   │   ├── Profile.kt          ← User, Subscription
│           │   │   ├── PurchasedService.kt          ← User, Subscription
│           │   │   └── AccountOrder.kt     ← AccountOrder, OrderItem
│           │   ├── repository/             ← Interfaces consommées par les ViewModels
│           │   │   ├── AuthRepository.kt
│           │   │   ├── UserRepository.kt
│           │   │   ├── ServiceRepository.kt
│           │   │   └── OrderHistoryRepository.kt
│           │   └── usecase/               ← Logique métier isolée
│           │
│           ├── mock/                       ← Couche mock (actif si MOCK_API=true)
│           │   ├── MockInitializer.kt
│           │   ├── factories/
│           │   │   └── MockFactories.kt
│           │   ├── handlers/
│           │   │   ├── AuthHandlers.kt       ← login, register, otp, confirm-email
│           │   │   ├── TwoFactorHandlers.kt  ← 2fa/setup, 2fa/confirm
│           │   │   ├── UserHandlers.kt
│           │   │   └── OrderHandlers.kt
│           │   └── registry/
│           │       ├── MockRegistry.kt
│           │       └── MockEngine.kt
│           │
│           └── ui/
│               ├── core/
│               │   ├── Navigation.kt           ← Destination (8 routes), NavHost
│               │   ├── theme/
│               │   │   ├── Color.kt            ← Palette Cyna (oklch → sRGB)
│               │   │   ├── Theme.kt            ← Light/DarkColorScheme, AppTheme
│               │   │   ├── Type.kt             ← CynaTypography
│               │   │   └── ThemeManager.kt     ← Light | Dark | System
│               │   └── components/ui/
│               │       ├── AccountSection.kt   ← Shell tabs Orders / Profile
│               │       ├── AuthCard.kt         ← Carte blanche auth (rounded-2xl)
│               │       ├── BottomNavBar.kt
│               │       ├── CancelDialog.kt
│               │       ├── FieldWithLabel.kt
│               │       ├── KLink.kt
│               │       ├── SectionCard.kt
│               │       ├── layout/             ← MainScaffold, Header, Footer, Spacers…
│               │       ├── order/              ← OrderRow, YearGroup, YearFilterRow
│               │       ├── services/           ← ServiceTelemetryCard
│               │       └── profile/            ← SubscriptionRow
│               │
│               └── screens/
│                   ├── auth/
│                   │   ├── AuthViewModel.kt            ← Login + Register (partagé)
│                   │   ├── LoginScreen.kt
│                   │   ├── RegisterScreen.kt
│                   │   ├── confirmemail/
│                   │   │   ├── ConfirmEmailScreen.kt
│                   │   │   └── ConfirmEmailViewModel.kt
│                   │   ├── forgotpassword/
│                   │   │   ├── ForgotPasswordScreen.kt
│                   │   │   └── ForgotPasswordViewModel.kt
│                   │   ├── resetpassword/
│                   │   │   ├── ResetPasswordScreen.kt
│                   │   │   └── ResetPasswordViewModel.kt
│                   │   └── security2fa/
│                   │       ├── Security2FAScreen.kt
│                   │       └── Security2FAViewModel.kt ← + TwoFactorRepository interface
│                   ├── ordershistory/
│                   │   ├── OrderHistoryViewModel.kt
│                   │   └── OrderHistoryScreen.kt
│                   ├── services/
│                   │   ├── ServicesViewModel.kt
│                   │   └── ServicesScreen.kt
│                   └── profile/
│                       ├── ProfileViewModel.kt
│                       └── ProfileScreen.kt
│
├── gradle/
│   └── libs.versions.toml              ← Catalogue de versions centralisé
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Conventions de nommage

### Fichiers

| Type | Convention | Exemple |
|------|-----------|---------|
| Activité | `PascalCase.kt` | `MainActivity.kt` |
| Écran Composable | `PascalCaseScreen.kt` | `LoginScreen.kt` |
| ViewModel | `PascalCaseViewModel.kt` | `ProfileViewModel.kt` |
| Contracts (interface) | dans le fichier ViewModel | `ProfileContracts` |
| Repository (interface) | `PascalCaseRepository.kt` | `UserRepository.kt` |
| Repository (impl) | `PascalCaseRepositoryImpl.kt` | `UserRepositoryImpl.kt` |
| API (remote) | `PascalCaseAPI.kt` | `AuthAPI.kt` |
| DTO | `PascalCaseDto.kt` | `AccountOrderDto.kt` |
| Handler mock | `PascalCaseHandlers.kt` | `UserHandlers.kt` |

### Packages

```
com.cyna.app
├── data.dto            → objets réseau (@Serializable, internal)
├── data.local          → persistance locale (SharedPreferences)
├── data.remote         → classes API + HttpClient
├── data.repository     → implémentations (internal)
├── data.util           → helpers techniques (VibrationHelper)
├── di                  → module Koin
├── domain.model        → entités métier (data class pures)
├── domain.repository   → interfaces des repositories
├── domain.usecase      → cas d'usage
├── mock                → couche mock (factories, handlers, registry)
└── ui                  → Compose (core + screens)
```

---

## Règles fondamentales

### Couche domaine — isolation totale

```kotlin
// ✅ Correct — modèle domaine pur
data class User(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isEmailVerified: Boolean
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val initials: String get() = listOfNotNull(firstName.firstOrNull(), lastName.firstOrNull())
        .joinToString("") { it.uppercase() }.ifEmpty { "?" }
}

// ❌ Interdit dans domain/model/
@Serializable          // ← annotation réseau
data class User(...)

import android.content.Context   // ← dépendance Android
```

### DTOs — internes à la couche data

```kotlin
// ✅ Toujours internal + @Serializable
@Serializable
internal data class AccountOrderDto(
    val id: Int,
    val status: String,
    val totalAmount: Double,
    val createdAt: String,
    val invoiceUrl: String? = null,
    val items: List<OrderItemDto> = emptyList()
)
```

### Composables — séparation écran / contenu

Chaque écran est découpé en deux composables dans le même fichier :

```kotlin
// 1. Composable PUBLIC — reçoit NavController + injecte le ViewModel
@Composable
fun ProfileScreen(navController: NavController) {
    KScreen(
        viewModel = viewModel<ProfileViewModel>(),
        navController = navController
    ) { state, vm ->
        ProfileContent(state = state, onSaveProfile = vm::saveProfile, /* … */)
    }
}

// 2. Composable PRIVÉ — pur, testable, prévisualisable
@Composable
private fun ProfileContent(
    state: ProfileContracts.UiState = ProfileContracts.UiState(),
    onSaveProfile: () -> Unit = {},
    // … autres callbacks
) {
    // Jamais de ViewModel ici — uniquement state + lambdas
}
```

### Couleurs — toujours via le thème

```kotlin
// ✅
Text(color = MaterialTheme.colorScheme.primary)
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface))

// ❌
Text(color = Color(0xFF562BF5))
```

---

## Gradle — Version Catalog

Toutes les dépendances passent par `gradle/libs.versions.toml`. Ne jamais écrire de version en dur dans un fichier `build.gradle.kts`.

```toml
# Déclarer la version
[versions]
ktor = "3.5.0"

# Déclarer la librairie
[libraries]
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }

# Référencer dans build.gradle.kts
implementation(libs.ktor.client.core)
```

### Variables d'environnement (`local.properties`)

```properties
# Activer le mode mock (aucune requête réseau)
MOCK_API=true

# URL de base — 10.0.2.2 = hôte depuis l'émulateur AVD
BASE_URL=https://10.0.2.2:7169/
```

Ces valeurs sont lues dans `app/build.gradle.kts` et exposées via `BuildConfig` :

```kotlin
buildConfigField("Boolean", "MOCK_API", localProps.getProperty("MOCK_API", "false"))
buildConfigField("String",  "BASE_URL", "\"${localProps.getProperty("BASE_URL", "https://api.staging.projet-cyna.fr/")}\"")
```

Le variant `release` écrase toujours ces valeurs :

```kotlin
buildTypes {
    release {
        buildConfigField("Boolean", "MOCK_API", "false")
        buildConfigField("String",  "BASE_URL", "\"https://api.projet-cyna.fr/\"")
    }
}
```