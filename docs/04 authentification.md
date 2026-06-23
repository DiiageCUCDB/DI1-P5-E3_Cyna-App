# Authentification — Cookie JWT, OTP & 2FA

## Architecture complète

```
┌──────────────────────────────────────────────────────────────────┐
│                      Flux d'authentification                     │
│                                                                  │
│  LoginScreen ──────────────────────────────────────────────┐     │
│  RegisterScreen ──→ ConfirmEmailScreen                     │     │
│  ForgotPasswordScreen ──→ ResetPasswordScreen              │     │
│  ProfileScreen ──→ Security2FAScreen                       │     │
│                                                            │     │
│  AuthViewModel (Login/Register)                            │     │
│  ConfirmEmailViewModel                                     │     │
│  ForgotPasswordViewModel                                   │     │ 
│  ResetPasswordViewModel                                    │     │
│  Security2FAViewModel                                      │     │
└────────────────────────────┬───────────────────────────────┘     │
                             │                                     │
             AuthRepository (interface domain)                     │
                     │                                             │
             AuthRepositoryImpl                                    │
                     ├── AuthAPI ───────────────────────────────┐  │
                     │   ├── POST /auth/login                   │  │
                     │   ├── POST /auth/register                │  │
                     │   ├── POST /auth/logout                  │  │
                     │   ├── GET  /auth/me                      │  │
                     │   ├── POST /auth/forgot-password         │  │
                     │   ├── POST /auth/reset-password          │  │
                     │   └── POST /auth/confirm-email           │  │
                     │                                          │  │
                     └── SessionManager ←── CookieStorage       │  │
                                 └── SharedPreferences          │  │
                                                                │  │
             TwoFactorRepository (interface)                    │  │
                     └── TwoFactorRepositoryImpl                │  │
                                 └── TwoFactorAPI ──────────────┘  │
                                         ├── POST /auth/2fa/setup  │
                                         └── POST /auth/2fa/confirm│
```

---

## Flux de connexion

```
1. Utilisateur saisit email + password → LoginScreen
           │
2. AuthViewModel.login(onLoginSuccess)
           │
3. AuthRepositoryImpl.login(LoginRequest)
           │
4. AuthAPI.post("auth/login")
           │
   ┌───────────────────────────────────────────────┐
   │ Body  : { "message": "Connexion réussie." }   │
   │ Cookie: Set-Cookie: cyna_token=<jwt>; HttpOnly│
   │ Cookie: Set-Cookie: cyna_refresh_token=<jwt>  │
   └───────────────────────────────────────────────┘
           │
5. SessionManagerCookieStorage.addCookie()
   → SessionManager.saveTokens(token, refreshToken)
           │
6. Mode mock : si token vide → saveTokens("mock-session-token", …)
           │
7. UserAPI.getMe() → SessionManager.saveUser(user)
           │
8. NavHost observe token → navigate(OrdersHistory)
```

---

## Flux d'inscription

```
RegisterScreen
    │  saisit prénom, nom, email, mot de passe (critères de force)
    ▼
AuthViewModel.register(onSuccess = { onRegisterSuccess(email) })
    │
    └─ AuthAPI.post("auth/register") → MessageResponse
            │
            ▼
    Navigation vers ConfirmEmailScreen
    (email pré-rempli via URL encoded : confirm-email?email=xxx)
```

---

## Flux OTP — Mot de passe oublié

```
ForgotPasswordScreen
    │  email saisi
    ▼
ForgotPasswordViewModel.submit()
    │
    └─ AuthRepository.forgotPassword(email)
            │
            └─ AuthAPI.post("auth/forgot-password")
                    │
            Réponse toujours 200 (anti-énumération)
            → état submitted = true → affichage carte "Vérifiez votre boîte mail"
                    │
    Utilisateur clique "Saisir le code reçu →"
                    │
    Navigate → ResetPasswordScreen(email pré-rempli)
```

```
ResetPasswordScreen
    │  email + code OTP (InputOTP 3+3) + nouveau mot de passe
    ▼
ResetPasswordViewModel.submit()
    │  valide : email valide + code 6 chiffres + mot de passe fort (PWD_RULES)
    │
    └─ AuthRepository.resetPassword(email, code, newPassword)
            │
            └─ AuthAPI.post("auth/reset-password", ResetPasswordRequest)
                    │
            Succès → état success = true → carte "Mot de passe réinitialisé"
            → bouton "Se connecter" → navigate(Login) { popUpTo(0) }
```

---

## Flux OTP — Confirmation d'email

```
ConfirmEmailScreen (après inscription ou depuis profil)
    │  email pré-rempli + code OTP (InputOTP 3+3)
    ▼
ConfirmEmailViewModel.submit()
    │
    └─ AuthRepository.confirmEmail(email, code)
            │
            └─ AuthAPI.post("auth/confirm-email", ConfirmEmailRequest)
                    │
            Succès → état success = true → carte "Email confirmé !"
            → bouton "Continuer vers l'accueil" → onNavigateToLogin()

Renvoi de code :
ConfirmEmailViewModel.resend()
    └─ AuthRepository.forgotPassword(email)   ← réutilise le même endpoint
```

---

## Flux 2FA — Double authentification (Admin/SuperAdmin uniquement)

```
ProfileScreen (rôle == "Admin" || "SuperAdmin")
    │  bouton "Configurer" dans la section Sécurité
    ▼
Security2FAScreen
    │
    ▼
Security2FAViewModel.init → loadSetup()
    │
    └─ TwoFactorRepository.setup()
            │
            └─ TwoFactorAPI.post("auth/2fa/setup")
                    │
            TwoFactorSetupDto { secret, otpAuthUrl }
                    │
            Étape 1 : affiche QR code (qrserver.com) + clé secrète à copier
                    │
    Utilisateur scanne avec app TOTP (Google Authenticator, Authy…)
                    │
            Étape 2 : saisie code TOTP (InputOTP 3+3)
                    │
Security2FAViewModel.confirm()
    │
    └─ TwoFactorRepository.confirm(totpCode)
            │
            └─ TwoFactorAPI.post("auth/2fa/confirm", { totpCode })
                    │
            Succès → état activated = true → carte "2FA activé"
```

---

## SessionManager — API publique

```kotlin
class SessionManager(context: Context) {

    // Observables (StateFlow)
    val user: StateFlow<UserDto?>
    val token: StateFlow<String?>          // null = non authentifié
    val refreshToken: StateFlow<String?>

    // Écriture
    fun saveTokens(token: String, refreshToken: String)
    fun saveUser(user: UserDto)
    fun saveSession(user: UserDto, token: String, refreshToken: String)

    // Lecture
    fun isAuthenticated(): Boolean

    // Déconnexion — efface tout (SharedPreferences + StateFlow)
    fun clearSession()
}
```

---

## Gestion des 401

| Route | Comportement |
|-------|-------------|
| `POST /auth/login` ou `/auth/register` | Toast "Connexion échouée" + message API. Session non touchée. |
| Toute autre route protégée | `SessionManager.clearSession()` + toast "Session expirée" → Login. |

---

## DTOs d'authentification

### Auth standard

| Classe | Sens | Endpoint |
|--------|------|---------|
| `LoginRequest` | → API | `POST /auth/login` |
| `RegisterRequest` | → API | `POST /auth/register` |
| `RefreshTokenRequest` | → API | `POST /auth/logout` |
| `MessageResponse` | ← API | login, register, logout, otp |
| `UserDto` | ← API | `GET /auth/me` |

### OTP

| Classe | Sens | Endpoint |
|--------|------|---------|
| `ForgotPasswordRequest` | → API | `POST /auth/forgot-password` |
| `ResetPasswordRequest` | → API | `POST /auth/reset-password` |
| `ConfirmEmailRequest` | → API | `POST /auth/confirm-email` |

### 2FA

| Classe | Sens | Endpoint |
|--------|------|---------|
| `TwoFactorSetupDto` | ← API | `POST /auth/2fa/setup` |
| `TotpConfirmRequest` | → API | `POST /auth/2fa/confirm` (interne à TwoFactorAPI) |

---

## AuthRepository — interface complète

```kotlin
interface AuthRepository {
    suspend fun login(request: LoginRequest): MessageResponse
    suspend fun register(request: RegisterRequest): MessageResponse
    suspend fun logout()
    suspend fun forgotPassword(email: String): MessageResponse
    suspend fun resetPassword(email: String, code: String, newPassword: String): MessageResponse
    suspend fun confirmEmail(email: String, code: String): MessageResponse
}
```

---

## TwoFactorRepository — interface

```kotlin
// Définie dans Security2FAViewModel.kt, implémentée par TwoFactorRepositoryImpl
interface TwoFactorRepository {
    suspend fun setup(): TwoFactorSetupDto
    suspend fun confirm(totpCode: String)
}
```

---

## Règles de mot de passe (PWD_RULES)

Définies dans `ResetPasswordViewModel.kt`, utilisées aussi dans `AuthContracts.UiState` :

```kotlin
val PWD_RULES = listOf(
    PwdRule("length",    "8 caractères minimum")  { it.length >= 8 },
    PwdRule("uppercase", "Une majuscule")          { it.any(Char::isUpperCase) },
    PwdRule("number",    "Un chiffre")             { it.any(Char::isDigit) },
    PwdRule("special",   "Un caractère spécial")  { p -> p.any { !it.isLetterOrDigit() } }
)
```

---

## Configuration locale

```properties
# local.properties
MOCK_API=false
BASE_URL=https://10.0.2.2:7169/
```

> `10.0.2.2` = adresse hôte depuis l'émulateur Android AVD.

### SSL auto-signé en debug

```kotlin
// AppModule.kt — uniquement DEBUG=true && MOCK_API=false
OkHttp.create {
    preconfigured = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllTrustManager())
        .hostnameVerifier { _, _ -> true }
        .build()
}
```