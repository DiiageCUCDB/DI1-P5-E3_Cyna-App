# Authentification — Cookie JWT

## Architecture

L'API Cyna utilise une authentification **basée sur des cookies HTTP** (`HttpOnly`). Les tokens ne transitent jamais dans le corps des réponses JSON : ils sont injectés par le serveur via des headers `Set-Cookie` et renvoyés automatiquement par Ktor sur chaque requête.

```
LoginScreen / RegisterScreen
    └── AuthViewModel
            └── AuthRepository (interface domain)
                    └── AuthRepositoryImpl
                            ├── AuthAPI  ──────────── POST /auth/login   → Set-Cookie: cyna_token
                            │                         POST /auth/register → 201 { message }
                            │                         POST /auth/logout
                            │                         GET  /auth/me       → UserDto
                            └── SessionManager ←──── SessionManagerCookieStorage
                                    └── SharedPreferences "cyna_prefs"
```

---

## Flux de connexion

1. L'utilisateur saisit ses identifiants sur `LoginScreen`.
2. `AuthViewModel.login()` appelle `AuthRepository.login(LoginRequest)`.
3. `AuthRepositoryImpl` délègue à `AuthAPI.login()` qui envoie `POST /auth/login`.
4. La réponse HTTP contient :
   - Corps : `{ "message": "Connexion réussie." }`
   - Headers : `Set-Cookie: cyna_token=<jwt>; HttpOnly` et `Set-Cookie: cyna_refresh_token=<jwt>; HttpOnly`
5. Le plugin `HttpCookies` de Ktor intercepte les `Set-Cookie` et appelle `SessionManagerCookieStorage.addCookie()`.
6. `SessionManagerCookieStorage` persiste les valeurs dans `SessionManager.saveTokens()`.
7. `AuthRepositoryImpl` appelle `UserAPI.getMe()` pour charger le profil et le stocker via `SessionManager.saveUser()`.
8. `Navigation.kt` observe `sessionManager.token` : `token != null` → navigation vers l'écran principal.

```kotlin
// Navigation.kt — pilotage par le token
val token by sessionManager.token.collectAsState()
val isAuthenticated = token != null
```

---

## Flux de déconnexion

1. `AuthRepository.logout()` appelle `AuthAPI.logout(refreshToken)`.
2. Dans le bloc `finally`, `SessionManager.clearSession()` est toujours appelé (même si le serveur répond 5xx).
3. `token` passe à `null` → `Navigation.kt` bascule vers l'écran de connexion.

---

## Gestion des 401

Le `HttpCallValidator` dans `HttpClient.kt` distingue deux cas :

| Requête | Comportement |
|---------|--------------|
| `POST /auth/login` ou `/auth/register` | Toast "Connexion échouée" + message d'erreur API. La session n'est pas touchée. |
| Toute autre route protégée | `SessionManager.clearSession()` + toast "Session expirée" → déconnexion immédiate. |

```kotlin
// HttpClient.kt — logique de différenciation
val path = response.call.request.url.encodedPath
val isAuthEndpoint = path.endsWith("/auth/login") || path.endsWith("/auth/register")
```

---

## SessionManagerCookieStorage

`SessionManagerCookieStorage` est une implémentation de l'interface `CookiesStorage` de Ktor qui fait le lien entre le cycle de vie des cookies Ktor et la persistance dans `SessionManager`.

```kotlin
// Lecture : cookies envoyés avec chaque requête
override suspend fun get(requestUrl: Url): List<Cookie> = buildList {
    sessionManager.token.value?.takeIf { it.isNotEmpty() }
        ?.let { add(Cookie(name = "cyna_token", value = it)) }
    sessionManager.refreshToken.value?.takeIf { it.isNotEmpty() }
        ?.let { add(Cookie(name = "cyna_refresh_token", value = it)) }
}

// Écriture : Set-Cookie reçu du serveur
override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
    when (cookie.name) {
        "cyna_token"         -> sessionManager.saveTokens(cookie.value, sessionManager.refreshToken.value ?: "")
        "cyna_refresh_token" -> sessionManager.saveTokens(sessionManager.token.value ?: "", cookie.value)
    }
}
```

---

## Mode mock

Le `MockEngine` ne renvoie pas de `Set-Cookie`. Pour maintenir la cohérence de la navigation en mode mock, `AuthRepositoryImpl.login()` vérifie après l'appel si `SessionManager.token` est encore vide et injecte un token fictif :

```kotlin
if (sessionManager.token.value.isNullOrEmpty()) {
    sessionManager.saveTokens("mock-session-token", "mock-refresh-token")
}
```

Les handlers mock dans `mock/handlers/AuthHandlers.kt` renvoient `MessageResponse("Connexion réussie.")`, ce qui correspond exactement au format de l'API réelle.

---

## Configuration locale (développement)

### `local.properties`

```properties
# Activer/désactiver le mode mock (pas de requêtes réseau)
MOCK_API=false

# URL de l'API — utiliser l'IP de passerelle de l'émulateur pour joindre le backend local
BASE_URL=https://10.0.2.2:7169/
```

> `10.0.2.2` est l'adresse hôte standard dans l'émulateur Android AVD.  
> Pour un appareil physique sur le même réseau, utiliser l'IP locale de la machine (ex. `192.168.x.x`).

### SSL en mode debug

L'API locale tourne avec un certificat auto-signé. Le moteur CIO de Ktor ne permet pas de contourner la vérification d'hostname via un `TrustManager` — il faut utiliser OkHttp avec `preconfigured` :

```kotlin
// AppModule.kt — moteur debug seulement
OkHttp.create {
    preconfigured = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllTrustManager())
        .hostnameVerifier { _, _ -> true }
        .build()
}
```

Ce bypass n'est actif que si `BuildConfig.DEBUG == true` et `BuildConfig.MOCK_API == false`. Il n'est jamais inclus dans les builds de production (variant `release`).

### Valeurs de production

Dans `app/build.gradle.kts`, le variant `release` écrase les valeurs de `local.properties` :

```kotlin
buildTypes {
    release {
        buildConfigField("Boolean", "MOCK_API", "false")
        buildConfigField("String",  "BASE_URL", "\"https://api.projet-cyna.fr/\"")
    }
}
```

---

## DTOs d'authentification

| Classe | Usage |
|--------|-------|
| `LoginRequest` | Corps de `POST /auth/login` |
| `RegisterRequest` | Corps de `POST /auth/register` |
| `RefreshTokenRequest` | Corps de `POST /auth/logout` (contient le refresh token) |
| `UserDto` | Réponse de `GET /auth/me` — persisté dans `SessionManager` |
| `AuthResponse` | Reservé — non utilisé par les endpoints actuels (tokens passent par cookies) |
| `MessageResponse` | Réponse de login, register, logout (`{ "message": "..." }`) |
