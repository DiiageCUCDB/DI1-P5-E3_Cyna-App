# Architecture

## Vue d'ensemble — Clean Architecture

Le projet suit une **Clean Architecture à 3 couches** avec une séparation stricte des responsabilités.

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│                                                         │
│   Composables Compose  ←→  ViewModel (KViewModel)       │
│   LoginScreen              AuthViewModel                │
│   OrderHistoryScreen       OrderHistoryViewModel        │
│   ProfileScreen            ProfileViewModel             │
│                                                         │
│   Kindling UI (KButton, KInput, KBadge, Skeleton…)      │
└────────────────────────────┬────────────────────────────┘
                             │  interfaces domain
┌────────────────────────────▼────────────────────────────┐
│                    Domain Layer                         │
│                                                         │
│   Interfaces Repository    Modèles métier purs          │
│   AuthRepository           User                         │
│   UserRepository           Subscription                 │
│   OrderHistoryRepository   AccountOrder, OrderItem      │
│                                                         │
│   (aucune dépendance Android ni réseau)                 │
└────────────────────────────┬────────────────────────────┘
                             │  implémentations
┌────────────────────────────▼────────────────────────────┐
│                     Data Layer                          │
│                                                         │
│   RepositoryImpl   →  XxxAPI  →  HttpClient (Ktor)      │
│                                        │                │
│   SessionManager (SharedPreferences)   │                │
│   SessionManagerCookieStorage          │                │
│                                        ▼                │
│                              Backend REST               │
│                         (ou MockEngine)                 │
└─────────────────────────────────────────────────────────┘
```

---

## Flux de données complet

### Exemple : chargement du profil utilisateur

```
ProfileScreen
  │
  └─ KScreen { state, vm → ProfileContent(state) }
          │
          ▼
  ProfileViewModel.init()
          │
          └─ fetchData { userRepository.getMe() }
                  │
                  ▼
          UserRepositoryImpl.getMe()
                  │
                  └─ UserAPI.getMe()
                          │
                          └─ HttpClient.get("user/profile")
                                  │
                          ┌───────┴────────────┐
                          │ MOCK_API=true       │ MOCK_API=false
                          ▼                    ▼
                    MockEngine          Ktor CIO / OkHttp
                    MockRegistry        Backend REST
                    UserHandlers        GET /user/profile
                          │                    │
                          └───────┬────────────┘
                                  ▼
                          UserDto (JSON → @Serializable)
                                  │
                          UserRepositoryImpl
                          .toDomain() → User
                                  │
                          ProfileViewModel
                          updateState { copy(user = user) }
                                  │
                          StateFlow<UiState>
                                  │
                          ProfileContent recompose
```

---

## Flux d'authentification

```
LoginScreen
    │  saisie email + password
    ▼
AuthViewModel.login(onSuccess)
    │
    └─ AuthRepository.login(LoginRequest)
            │
            └─ AuthAPI.post("auth/login")
                    │
                    ▼
            API répond :
            Body  : { "message": "Connexion réussie." }
            Cookie: Set-Cookie: cyna_token=<jwt>; HttpOnly
            Cookie: Set-Cookie: cyna_refresh_token=<jwt>; HttpOnly
                    │
            SessionManagerCookieStorage.addCookie()
            SessionManager.saveTokens(token, refreshToken)
                    │
            AuthRepositoryImpl : si token vide (mode mock)
            → saveTokens("mock-session-token", "mock-refresh-token")
                    │
            UserAPI.getMe() → SessionManager.saveUser(user)
                    │
    NavHost observe sessionManager.token
    token != null → navigate(OrdersHistory)
```

---

## Gestion des erreurs HTTP

```
HttpCallValidator (dans HttpClient.kt)
        │
        ├─ 2xx  → OK, continuer
        │
        ├─ 401  ┬─ path == /auth/login ou /auth/register
        │       │    → Toast "Connexion échouée" + message API
        │       │    → throw HttpException.ClientError(401, msg)
        │       │
        │       └─ toute autre route protégée
        │            → SessionManager.clearSession()
        │            → Toast "Session expirée"
        │            → throw HttpException.ClientError(401, "Session expirée")
        │
        ├─ 4xx  → VibrationHelper.warning()
        │         → Toast "Client error (4xx)" + message API
        │         → throw HttpException.ClientError(status, msg)
        │
        └─ 5xx  → VibrationHelper.error()
                  → Toast "Server error (5xx)" + message API
                  → throw HttpException.ServerError(status, msg)
```

---

## Sélection du moteur HTTP

```
AppModule — single<HttpClientEngine>
        │
        ├─ BuildConfig.MOCK_API == true
        │       → buildMockEngine(delayMs = 400L)
        │         (MockRegistry, aucune requête réseau)
        │
        ├─ BuildConfig.DEBUG == true  (et MOCK_API == false)
        │       → OkHttp avec SSL bypass total
        │         (certificat auto-signé local, émulateur 10.0.2.2)
        │
        └─ Production (release)
                → CIO.create()
                  (moteur Kotlin natif, léger)
```

---

## Structure MVI par écran

Chaque écran suit le même pattern contracts / ViewModel / Screen :

```
XxxContracts (interface)
    └─ UiState (data class)    ← état immuable, valeurs par défaut

XxxViewModel : KViewModel<XxxContracts.UiState>
    └─ init { load() }
    └─ fetchData { repository.… }
    └─ updateState { copy(…) }

XxxScreen (public)    ← reçoit NavController
    └─ KScreen(viewModel, navController) { state, vm →
           XxxContent(state, vm::action, …)
       }

XxxContent (private)  ← reçoit uniquement UiState + lambdas
```

### ViewModels auth existants

| ViewModel | Écran(s) | Repository injecté |
|-----------|----------|--------------------|
| `AuthViewModel` | `LoginScreen`, `RegisterScreen` | `AuthRepository` |
| `ForgotPasswordViewModel` | `ForgotPasswordScreen` | `AuthRepository` |
| `ResetPasswordViewModel` | `ResetPasswordScreen` | `AuthRepository` |
| `ConfirmEmailViewModel` | `ConfirmEmailScreen` | `AuthRepository` |
| `Security2FAViewModel` | `Security2FAScreen` | `TwoFactorRepository` |
| `ProfileViewModel` | `ProfileScreen` | `UserRepository` |
| `OrderHistoryViewModel` | `OrderHistoryScreen` | `OrderHistoryRepository`, `UserRepository` |

---

## Décisions d'architecture notables

### Pourquoi les cookies plutôt que Bearer ?

L'API Cyna utilise des cookies `HttpOnly` pour les tokens JWT. Avantages : les tokens ne transitent jamais dans le corps des réponses JSON et sont automatiquement envoyés par Ktor via le plugin `HttpCookies`. La `SessionManagerCookieStorage` fait le pont entre Ktor et `SharedPreferences`.

### Pourquoi OkHttp en debug et CIO en production ?

CIO ne permet pas de court-circuiter la vérification d'hostname via un `TrustManager` personnalisé. L'API locale de développement tourne avec un certificat auto-signé → OkHttp avec `hostnameVerifier { _, _ -> true }` uniquement quand `DEBUG=true && MOCK_API=false`. Ce code est absent des builds release.

### Pourquoi KoinComponent + `by inject()` dans les ViewModels ?

Les ViewModels s'instancient via `viewModel<MyViewModel>()` de Compose, qui passe par le framework Android (pas par Koin directement). Étendre `KoinComponent` et utiliser `by inject()` à l'intérieur du ViewModel permet de récupérer les dépendances depuis le conteneur Koin sans les déclarer en paramètre de constructeur, évitant d'avoir à enregistrer chaque ViewModel dans `AppModule`.