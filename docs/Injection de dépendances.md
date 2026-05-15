# Injection de dépendances — Koin

## Vue d'ensemble

Le projet utilise **Koin 3.5** pour l'injection de dépendances. Koin est un framework léger, sans génération de code (pas d'annotation processing), adapté à Kotlin et Compose.

```
Application.onCreate()
    └── startKoin { modules(appModule) }
            └── AppModule.kt
                    ├── HttpClient (singleton)
                    ├── XxxAPI (singleton)
                    └── XxxRepository (singleton)
```

---

## Initialisation

Koin est démarré dans `Application.kt` avant que toute activité ne soit créée :

```kotlin
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.ERROR)  // Level.DEBUG en développement
            androidContext(this@Application)
            modules(appModule)
        }
    }
}
```

`androidContext` rend le contexte Android disponible pour les dépendances qui en ont besoin (ex. bases de données, préférences).

---

## Module principal — AppModule

Toutes les dépendances sont déclarées dans `di/AppModule.kt` :

```kotlin
private const val API_URL = "http://your-backend.com/api/"

val appModule = module {

    // HttpClient — singleton partagé par toutes les APIs
    single<HttpClient> {
        createHttpClient(baseUrl = API_URL)
    }

    // APIs réseau
    single { LoginAPI(get()) }
    single { ProductAPI(get()) }

    // Repositories
    single<LoginRepository>   { LoginRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
}
```

---

## Types de déclarations Koin

### `single` — Singleton

Une seule instance créée et réutilisée pour toute la durée de vie de l'application :

```kotlin
single<HttpClient> { createHttpClient(API_URL) }
single<ProductRepository> { ProductRepositoryImpl(get()) }
```

À utiliser pour : clients HTTP, repositories, services stateless.

### `factory` — Nouvelle instance à chaque injection

Une nouvelle instance créée à chaque fois que la dépendance est demandée :

```kotlin
factory { CreateProductUseCase(get()) }
```

À utiliser pour : cas d'usage, objets à état court.

### `viewModel` — ViewModel Koin

Pour les ViewModels gérés par Koin (alternative à `viewModel<>()` Compose) :

```kotlin
viewModel { LoginViewModel(get()) }
```

Cependant, le projet utilise actuellement `viewModel<LoginViewModel>()` de Compose + `KoinComponent` + `by inject()` dans le ViewModel, ce qui évite de déclarer chaque ViewModel dans le module.

---

## Injection dans les ViewModels

Les ViewModels étendent `KoinComponent` (via la classe de base `ViewModel<State>`) et accèdent aux dépendances via le délégué `by inject()` :

```kotlin
class ProductViewModel(application: Application)
    : ViewModel<ProductContracts.UiState>(ProductContracts.UiState(), application) {

    // Injection automatique depuis le conteneur Koin
    private val repository: ProductRepository by inject()
    private val analyticsService: AnalyticsService by inject()

    // ...
}
```

**Ne jamais passer de dépendances en paramètre du constructeur** si elles peuvent être injectées — cela rend les tests plus complexes sans apporter de bénéfice.

---

## Ajouter une nouvelle dépendance

### 1. Créer l'interface (domaine)

```kotlin
// domain/repository/NotificationRepository.kt
interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
}
```

### 2. Créer l'implémentation (data)

```kotlin
// data/repository/NotificationRepositoryImpl.kt
internal class NotificationRepositoryImpl(
    private val api: NotificationAPI
) : NotificationRepository {
    override suspend fun getNotifications() =
        api.getNotifications().map { it.toDomain() }
}
```

### 3. Déclarer dans AppModule

```kotlin
val appModule = module {
    // ... dépendances existantes

    // Nouvelle ressource
    single { NotificationAPI(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}
```

### 4. Injecter dans le ViewModel

```kotlin
class NotificationViewModel(application: Application)
    : ViewModel<NotificationContracts.UiState>(...) {

    private val repository: NotificationRepository by inject()
}
```

---

## Modules multiples (organisation avancée)

Pour les projets plus larges, séparer les modules par domaine fonctionnel :

```kotlin
// di/NetworkModule.kt
val networkModule = module {
    single<HttpClient> { createHttpClient(API_URL) }
}

// di/AuthModule.kt
val authModule = module {
    single { LoginAPI(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
}

// di/ProductModule.kt
val productModule = module {
    single { ProductAPI(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
}

// Application.kt
startKoin {
    modules(networkModule, authModule, productModule)
}
```

---

## Tests avec Koin

Pour les tests unitaires, remplacer les modules par des mocks :

```kotlin
class LoginViewModelTest : KoinTest {

    @Before
    fun setup() {
        startKoin {
            modules(module {
                single<LoginRepository> { FakeLoginRepository() }
            })
        }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `login success navigates to home`() {
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext())
        // ...
    }
}
```

---

## Dépannage courant

| Symptôme | Cause | Solution |
|----------|-------|----------|
| `NoBeanDefFoundException` | Dépendance non déclarée dans le module | Ajouter `single { ... }` dans `AppModule` |
| `InstanceAlreadyExistsException` | `startKoin` appelé deux fois | S'assurer que `startKoin` n'est appelé que dans `Application` |
| Crash `NullPointerException` sur `by inject()` | Koin non démarré avant l'accès | Vérifier que `Application.kt` est bien déclaré dans `AndroidManifest.xml` |
| Dépendance circulaire | A → B → A | Extraire une interface commune ou introduire un use case |