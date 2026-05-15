# ViewModel et gestion d'état (MVI)

## Principe général

Le projet utilise un pattern **MVI (Model-View-Intent)** adapté à Compose. Chaque écran expose :

- un `UiState` immuable observé par le composable
- des `UiAction` déclenchées par l'utilisateur
- des `Event` one-shot pour la navigation ou les messages ponctuels

```
Composable
    │  observe state
    │  envoie actions
    ▼
ViewModel
    │  met à jour l'état via updateState()
    │  envoie des événements via sendEvent()
    ▼
Screen (infrastructure)
    │  collecte les events
    │  route les Destination vers NavController
    └  passe les autres events à onEvent()
```

---

## ViewModel de base

Tous les ViewModels étendent `ViewModel<State>` défini dans `ui/core/ViewModel.kt` :

```kotlin
open class ViewModel<State>(initialState: State, application: Application)
    : AndroidViewModel(application), KoinComponent {

    val state: StateFlow<State>         // état observable
    val events: Flow<Any>               // événements one-shot

    protected fun updateState(block: State.() -> State)  // met à jour l'état
    protected fun sendEvent(obj: Any)                     // émet un événement

    fun <T> fetchData(source: suspend () -> T, onResult: Result<T>.() -> Unit)
    fun <T> collectData(source: suspend () -> Flow<T>, onResult: Result<T>.() -> Unit)
}
```

---

## Créer un ViewModel

### 1. Définir les contracts

Les contracts regroupent UiState, UiAction et Event dans une interface :

```kotlin
interface ProductContracts {
    data class UiState(
        val products: List<Product> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface UiAction {
        object LoadProducts : UiAction
        data class DeleteProduct(val id: String) : UiAction
        data class SelectProduct(val product: Product) : UiAction
    }

    sealed interface Event {
        data class NavigateToDetail(val productId: String) : Event
        data class ShowError(val message: String) : Event
    }
}
```

### 2. Implémenter le ViewModel

```kotlin
class ProductViewModel(
    application: Application
) : ViewModel<ProductContracts.UiState>(
    initialState = ProductContracts.UiState(),
    application = application
) {
    // Injection Koin via délégué
    private val repository: ProductRepository by inject()

    init {
        handleAction(ProductContracts.UiAction.LoadProducts)
    }

    fun handleAction(action: ProductContracts.UiAction) {
        when (action) {
            ProductContracts.UiAction.LoadProducts    -> loadProducts()
            is ProductContracts.UiAction.DeleteProduct -> deleteProduct(action.id)
            is ProductContracts.UiAction.SelectProduct -> onProductSelected(action.product)
        }
    }

    private fun loadProducts() {
        updateState { copy(isLoading = true, errorMessage = null) }

        fetchData(
            source = { repository.getProducts() },
            onResult = {
                onSuccess { products ->
                    updateState { copy(products = products, isLoading = false) }
                }
                onFailure { error ->
                    updateState { copy(isLoading = false, errorMessage = error.message) }
                }
            }
        )
    }

    private fun deleteProduct(id: String) {
        fetchData(
            source = { repository.deleteProduct(id) },
            onResult = {
                onSuccess {
                    updateState { copy(products = products.filter { it.id != id }) }
                }
                onFailure { error ->
                    sendEvent(ProductContracts.Event.ShowError(error.message ?: "Erreur"))
                }
            }
        )
    }

    private fun onProductSelected(product: Product) {
        sendEvent(ProductContracts.Event.NavigateToDetail(product.id))
    }
}
```

---

## Utiliser le ViewModel dans un composable

### Composable public (avec Screen)

```kotlin
@Composable
fun ProductScreen(navController: NavController) {
    Screen(
        viewModel = viewModel<ProductViewModel>(),
        navController = navController,
        onEvent = { state, viewModel, event ->
            when (event) {
                is ProductContracts.Event.NavigateToDetail -> {
                    navController.navigate("product/${event.productId}")
                }
                is ProductContracts.Event.ShowError -> {
                    // Afficher un snackbar ou toast
                }
            }
        }
    ) { state, viewModel ->
        ProductContent(
            state = state,
            handleAction = viewModel::handleAction
        )
    }
}
```

### Composable privé (contenu pur)

```kotlin
@Composable
private fun ProductContent(
    state: ProductContracts.UiState = ProductContracts.UiState(),
    handleAction: (ProductContracts.UiAction) -> Unit = {}
) {
    // Le contenu ne dépend que de state et handleAction
    // Facilement testable et prévisualisable
    if (state.isLoading) {
        CircularProgressIndicator()
        return
    }

    LazyColumn {
        items(state.products) { product ->
            ProductItem(
                product = product,
                onClick = { handleAction(ProductContracts.UiAction.SelectProduct(product)) },
                onDelete = { handleAction(ProductContracts.UiAction.DeleteProduct(product.id)) }
            )
        }
    }
}
```

---

## fetchData vs collectData

### `fetchData` — opération unique

Pour les appels réseau ponctuels (GET, POST, DELETE) :

```kotlin
fetchData(
    source = { repository.getProduct(id) },
    onResult = {
        onSuccess { product -> updateState { copy(product = product) } }
        onFailure { error  -> updateState { copy(errorMessage = error.message) } }
    }
)
```

### `collectData` — flux continu

Pour les flows Kotlin (données temps réel, base de données locale) :

```kotlin
collectData(
    source = { repository.observeProducts() },  // retourne un Flow<List<Product>>
    onResult = {
        onSuccess { products -> updateState { copy(products = products) } }
        onFailure { error    -> updateState { copy(errorMessage = error.message) } }
    }
)
```

Les deux méthodes :
- s'exécutent sur `Dispatchers.IO`
- livrent les résultats sur `Dispatchers.Main`
- catchent les exceptions et les passent comme `Result.failure`

---

## Règles à respecter

- Un seul `UiState` par écran — pas d'état fragmenté en plusieurs `StateFlow`
- `UiState` toujours une `data class` avec des valeurs par défaut
- Ne jamais exposer de `MutableStateFlow` depuis le ViewModel
- `sendEvent` pour les actions one-shot uniquement (navigation, toasts) — pas pour l'état persistant
- Les composables privés (`Content`) ne reçoivent jamais de ViewModel — seulement `state` et `handleAction`
- Initialiser les données dans `init {}` du ViewModel, pas dans un `LaunchedEffect` du composable