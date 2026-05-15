# Couche réseau — Ktor

## Vue d'ensemble

Tous les appels réseau passent par un `HttpClient` Ktor configuré dans `data/remote/HttpClient.kt` et injecté via Koin. Chaque ressource API dispose de sa propre classe dans `data/remote/`.

```
ViewModel
    └── Repository (interface domain)
            └── RepositoryImpl (data)
                    └── XxxAPI (remote)
                            └── HttpClient (Ktor)
                                    └── Backend REST
```

---

## Configuration du client HTTP

Le client est créé par `createHttpClient()` dans `data/remote/HttpClient.kt` :

```kotlin
fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create()
): HttpClient = HttpClient(engine) {
    // URL de base appliquée à toutes les requêtes
    defaultRequest { url(baseUrl) }

    // Désérialisation JSON via kotlinx.serialization
    install(ContentNegotiation) { json() }

    // Timeouts réseau
    install(HttpTimeout) {
        connectTimeoutMillis = 15000
        socketTimeoutMillis  = 15000
        requestTimeoutMillis = 15000
    }

    // Logs détaillés (désactiver en production)
    install(Logging) { level = LogLevel.ALL }

    // Normalisation des erreurs HTTP
    install(HttpCallValidator) {
        validateResponse { response ->
            when (response.status.value) {
                in 400..499 -> throw HttpException.ClientError(response.status.value, ...)
                in 500..599 -> throw HttpException.ServerError(response.status.value, ...)
            }
        }
    }
}
```

Le client est enregistré comme singleton dans Koin :

```kotlin
// di/AppModule.kt
single<HttpClient> { createHttpClient(baseUrl = API_URL) }
```

---

## Créer une classe API

Chaque ressource a sa propre classe dans `data/remote/`. Elle reçoit le `HttpClient` par injection.

```kotlin
// data/remote/ProductAPI.kt
internal class ProductAPI(private val client: HttpClient) {

    suspend fun getProducts(): List<ProductDto> =
        client.get("products")
              .accept(HttpStatusCode.OK)
              .body()

    suspend fun getProduct(id: String): ProductDto =
        client.get("products/$id")
              .accept(HttpStatusCode.OK)
              .body()

    suspend fun createProduct(dto: CreateProductDto): ProductDto =
        client.post("products") {
            setBodyJson(dto)
        }
        .accept(HttpStatusCode.Created)
        .body()

    suspend fun deleteProduct(id: String) =
        client.delete("products/$id")
              .accept(HttpStatusCode.NoContent)
}
```

### Fonctions utilitaires disponibles

```kotlin
// Définir le corps JSON avec Content-Type automatique
inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T)

// Vérifier que le code de statut est attendu (sinon lance HttpException.NotAccepted)
fun HttpResponse.accept(vararg codes: HttpStatusCode): HttpResponse
```

### Enregistrement dans Koin

```kotlin
// di/AppModule.kt
single { ProductAPI(get()) }
single<ProductRepository> { ProductRepositoryImpl(get()) }
```

---

## DTOs — Data Transfer Objects

Les DTOs sont des `data class` annotées `@Serializable` dans `data/dto/`. Ils ne contiennent que ce que l'API renvoie — pas de logique métier.

```kotlin
// data/dto/Product.kt
@Serializable
internal data class ProductDto(
    val id: String,
    val name: String,
    val priceMonthly: Double,
    val priceYearly: Double,
    val isAvailable: Boolean,
    val categoryId: String,
    val createdAt: String
)

@Serializable
internal data class CreateProductDto(
    val name: String,
    val priceMonthly: Double,
    val categoryId: String
)
```

**Conventions DTO :**
- Toujours `internal` — jamais exposés hors de la couche data
- Nommage `snake_case` si l'API le requiert → configurer le JSON serializer
- Champs optionnels en `String?` ou avec valeur par défaut
- Séparation DTO d'entrée / DTO de création (ne pas réutiliser le même)

---

## Gestion des erreurs

`HttpClient.kt` définit une hiérarchie d'exceptions :

```kotlin
sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted(message: String) : HttpException(message)   // statut inattendu
    class ClientError(val statusCode: Int, message: String) : HttpException(message)  // 4xx
    class ServerError(val statusCode: Int, message: String) : HttpException(message)  // 5xx
}
```

Ces exceptions remontent jusqu'au ViewModel via `fetchData` :

```kotlin
fetchData(
    source = { productAPI.getProduct(id) },
    onResult = {
        onSuccess { dto -> /* ... */ }
        onFailure { error ->
            when (error) {
                is HttpException.ClientError -> when (error.statusCode) {
                    404 -> updateState { copy(errorMessage = "Produit introuvable") }
                    403 -> sendEvent(Destination.Unauthorized)
                    else -> updateState { copy(errorMessage = error.message) }
                }
                is HttpException.ServerError ->
                    updateState { copy(errorMessage = "Erreur serveur, réessayez plus tard") }
                else ->
                    updateState { copy(errorMessage = "Erreur réseau") }
            }
        }
    }
)
```

---

## Authentification

Pour les routes authentifiées, ajouter un intercepteur dans `createHttpClient()` :

```kotlin
// Dans HttpClient.kt — à implémenter quand l'auth est disponible
defaultRequest {
    url(baseUrl)
    val token = tokenStore.getToken()  // ex. DataStore ou SharedPreferences
    if (token != null) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}
```

Le token doit être stocké dans une couche dédiée (ex. `data/local/TokenStore.kt`) injectée dans le client.

---

## Mapper DTO → Modèle domaine

Les implémentations de repository convertissent les DTOs en modèles domaine :

```kotlin
// data/repository/ProductRepositoryImpl.kt
internal class ProductRepositoryImpl(
    private val api: ProductAPI
) : ProductRepository {

    override suspend fun getProducts(): List<Product> =
        api.getProducts().map { it.toDomain() }

    override suspend fun getProduct(id: String): Product =
        api.getProduct(id).toDomain()
}

// Extension de mapping (dans le même fichier ou un fichier *Mapper.kt)
private fun ProductDto.toDomain() = Product(
    id           = id,
    name         = name,
    priceMonthly = priceMonthly,
    priceYearly  = priceYearly,
    isAvailable  = isAvailable,
    categoryId   = categoryId
)
```

**Règle :** les modèles domaine (`domain/model/`) ne doivent jamais contenir `@Serializable` ni dépendre d'Android.

---

## Ajouter une nouvelle ressource — procédure complète

**Étape 1 — DTO**
```kotlin
// data/dto/Notification.kt
@Serializable
internal data class NotificationDto(val id: String, val message: String, val read: Boolean)
```

**Étape 2 — Classe API**
```kotlin
// data/remote/NotificationAPI.kt
internal class NotificationAPI(private val client: HttpClient) {
    suspend fun getNotifications(): List<NotificationDto> =
        client.get("notifications").accept(HttpStatusCode.OK).body()

    suspend fun markAsRead(id: String): NotificationDto =
        client.post("notifications/$id/read").accept(HttpStatusCode.OK).body()
}
```

**Étape 3 — Interface repository**
```kotlin
// domain/repository/NotificationRepository.kt
interface NotificationRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun markAsRead(id: String): Notification
}
```

**Étape 4 — Modèle domaine**
```kotlin
// domain/model/Notification.kt
data class Notification(val id: String, val message: String, val read: Boolean)
```

**Étape 5 — Implémentation**
```kotlin
// data/repository/NotificationRepositoryImpl.kt
internal class NotificationRepositoryImpl(
    private val api: NotificationAPI
) : NotificationRepository {
    override suspend fun getNotifications() = api.getNotifications().map { it.toDomain() }
    override suspend fun markAsRead(id: String) = api.markAsRead(id).toDomain()
}
private fun NotificationDto.toDomain() = Notification(id, message, read)
```

**Étape 6 — Koin**
```kotlin
// di/AppModule.kt
single { NotificationAPI(get()) }
single<NotificationRepository> { NotificationRepositoryImpl(get()) }
```