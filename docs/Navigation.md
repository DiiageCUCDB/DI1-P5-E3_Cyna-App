# Navigation

## Vue d'ensemble

La navigation est gérée par **Jetpack Navigation Compose**. Toute la configuration est centralisée dans `ui/core/Navigation.kt`.

```
MainActivity
    └── App()
            └── NavHost(navController)
                    ├── SplashScreen    → route "splash"  (startDestination)
                    ├── LoginScreen     → route "login"
                    └── HomeScreen      → route "home"
```

---

## Destinations

Les destinations sont définies via une `sealed class` pour garantir la sûreté de type :

```kotlin
sealed class Destination(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    object Splash : Destination(route = "splash")
    object Home   : Destination(route = "home")
    object Login  : Destination(route = "login")
}
```

Avantages de cette approche :
- Pas de chaînes de caractères éparpillées dans le code
- Refactoring facile — changer la route à un seul endroit
- Autocomplétion IDE pour toutes les destinations

---

## Ajouter une destination

### Étape 1 — Déclarer la destination

```kotlin
// ui/core/Navigation.kt
sealed class Destination(...) {
    // ...
    object Catalog : Destination(route = "catalog")

    // Avec argument
    object ProductDetail : Destination(
        route = "product/{productId}",
        arguments = listOf(navArgument("productId") { type = NavType.StringType })
    )
}
```

### Étape 2 — Enregistrer dans le NavHost

```kotlin
@Composable
fun NavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destination.Splash.route,
        modifier = modifier
    ) {
        composable(Destination.Splash) { SplashScreen(navController) }
        composable(Destination.Home)  { HomeScreen(navController) }
        composable(Destination.Login) { LoginScreen(navController) }

        // Nouvel écran
        composable(Destination.Catalog) { CatalogScreen(navController) }

        // Avec argument
        composable(Destination.ProductDetail) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(navController, productId)
        }
    }
}
```

### Étape 3 — Créer l'écran

```kotlin
// ui/screens/catalog/CatalogScreen.kt
@Composable
fun CatalogScreen(navController: NavController) {
    Screen(
        viewModel = viewModel<CatalogViewModel>(),
        navController = navController
    ) { state, viewModel ->
        Content(state = state, handleAction = viewModel::handleAction)
    }
}
```

---

## Naviguer depuis un ViewModel

Les ViewModels ne reçoivent jamais le `NavController` directement. Ils envoient des **événements de navigation** via `sendEvent()` :

```kotlin
// Dans le ViewModel
private fun onLoginSuccess() {
    sendEvent(Destination.Home)  // ← envoie la destination comme événement
}
```

Le composable `Screen` intercepte automatiquement les événements de type `Destination` et déclenche la navigation :

```kotlin
// Dans Screen.kt (infrastructure — ne pas modifier)
LaunchedEffect(viewModel) {
    viewModel.events.onEach { event ->
        if (event is Destination) navController.navigate(destination = event)
        else onEvent(state, viewModel, event)
    }.collect()
}
```

---

## Naviguer avec options (popUpTo, launchSingleTop)

Pour contrôler la back stack lors de la navigation (ex. : supprimer le splash de la pile après login) :

```kotlin
// Extension disponible dans Navigation.kt
fun NavController.navigate(
    destination: Destination,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
)

// Utilisation dans un ViewModel (via sendEvent + gestionnaire custom)
// ou directement dans un composable :
navController.navigate(
    destination = Destination.Home,
    navOptions = NavOptions.Builder()
        .setPopUpTo(Destination.Splash.route, inclusive = true)
        .build()
)
```

Pour des navigations complexes avec options, utiliser `onEvent` dans `Screen` plutôt que `sendEvent(Destination)` :

```kotlin
// 1. Définir un événement custom dans les contracts
sealed interface Event {
    object NavigateToHomeAndClearStack : Event
}

// 2. L'émettre depuis le ViewModel
sendEvent(LoginContracts.Event.NavigateToHomeAndClearStack)

// 3. Le gérer dans le composable
Screen(
    viewModel = viewModel,
    navController = navController,
    onEvent = { _, _, event ->
        when (event) {
            LoginContracts.Event.NavigateToHomeAndClearStack -> {
                navController.navigate(
                    destination = Destination.Home,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(Destination.Splash.route, inclusive = true)
                        .build()
                )
            }
        }
    }
) { state, viewModel ->
    Content(state, viewModel::handleAction)
}
```

---

## Navigation avec arguments

### Passer un argument

```kotlin
// Destination avec argument
object ProductDetail : Destination(
    route = "product/{productId}",
    arguments = listOf(navArgument("productId") { type = NavType.StringType })
)

// Naviguer vers cette destination
navController.navigate("product/${product.id}")
```

### Récupérer un argument

```kotlin
composable(Destination.ProductDetail) { backStackEntry ->
    val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
    ProductDetailScreen(navController = navController, productId = productId)
}
```

### Dans le ViewModel (via SavedStateHandle)

```kotlin
class ProductDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel<ProductDetailContracts.UiState>(...) {
    private val productId: String = checkNotNull(savedStateHandle["productId"])
}
```

---

## Gestion du bouton Retour

Par défaut, `Screen` ne gère pas le bouton retour (retour système standard). Pour surcharger :

```kotlin
Screen(
    viewModel = viewModel,
    navController = navController,
    onBack = { state, viewModel ->
        // Logique custom sur retour (ex. confirmer abandon de formulaire)
        if (state.hasUnsavedChanges) {
            viewModel.handleAction(MyAction.ShowExitDialog)
        } else {
            navController.popBackStack()
        }
    }
) { state, vm ->
    Content(state, vm::handleAction)
}
```

Si `onBack` est `null` (défaut), le retour système fonctionne normalement.