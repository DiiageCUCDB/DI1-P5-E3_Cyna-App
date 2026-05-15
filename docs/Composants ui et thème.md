# Composants UI et thème

## Architecture UI

L'interface utilise **Jetpack Compose** avec **Material Design 3**. Les composants sont organisés en deux niveaux :

```
ui/core/components/
├── Screen.kt               ← Wrapper d'écran (infrastructure)
├── input/                  ← Composants interactifs
│   ├── PrimaryButton.kt
│   ├── PrimaryTextField.kt
│   ├── SearchBar.kt
│   ├── ToggleElementButton.kt
│   └── ToggleSwitch.kt
└── layout/                 ← Composants de mise en page
    ├── CenteredBox.kt
    ├── CenteredColumn.kt
    ├── MainScaffold.kt
    └── Spacers.kt
```

---

## Composant Screen

`Screen` est le wrapper standard pour tous les écrans. Il gère :
- l'observation du state
- la collecte des événements
- le gestionnaire de retour (optionnel)
- la gestion du focus clavier

```kotlin
@Composable
fun <State, VM: ViewModel<State>> Screen(
    viewModel: VM,
    navController: NavController,
    onBack: ((state: State, viewModel: VM) -> Unit)? = null,
    onEvent: (state: State, viewModel: VM, event: Any) -> Unit = { _, _, _ -> },
    content: @Composable (state: State, viewModel: VM) -> Unit
)
```

Utilisation standard :

```kotlin
@Composable
fun MyScreen(navController: NavController) {
    Screen(
        viewModel = viewModel<MyViewModel>(),
        navController = navController
    ) { state, viewModel ->
        MyContent(state = state, handleAction = viewModel::handleAction)
    }
}
```

---

## Composants input

### PrimaryButton

Bouton pleine largeur avec état de chargement et variantes de couleur.

```kotlin
PrimaryButton(
    onClick   = { handleAction(MyAction.Submit) },
    text      = "Valider",
    enabled   = state.isFormValid,
    isLoading = state.isSubmitting,
    variant   = ButtonVariant.PRIMARY   // PRIMARY | SECONDARY | SUCCESS | ERROR
)
```

| Variante | Couleur | Usage |
|----------|---------|-------|
| `PRIMARY` | `colorScheme.primary` | Action principale |
| `SECONDARY` | `colorScheme.secondary` | Action secondaire |
| `SUCCESS` | `colorScheme.primaryContainer` | Confirmation |
| `ERROR` | `colorScheme.error` | Action destructive |

### PrimaryTextField

Champ de texte outlined Material 3.

```kotlin
PrimaryTextField(
    value         = state.email,
    onValueChange = { handleAction(MyAction.EmailChanged(it)) },
    label         = "Email",
    keyboardType  = KeyboardType.Email,
    enabled       = !state.isLoading
)
```

### SearchBar

Barre de recherche avec icône et style pill.

```kotlin
SearchBar(
    value         = searchQuery,
    onValueChange = { searchQuery = it },
    placeholder   = "Rechercher un produit..."
)
```

### ToggleElementButton

Sélecteur binaire à deux segments (ex. mensuel / annuel).

```kotlin
ToggleElementButton(
    valueA        = "Mensuel",
    valueB        = "Annuel",
    currentValue  = state.billingPeriod,
    onValueChange = { handleAction(MyAction.PeriodChanged(it)) }
)
```

### PrimarySecondaryToggle

Switch animé avec changement de couleur selon la sélection.

```kotlin
PrimarySecondaryToggle(
    isPrimarySelected = state.isPrimary,
    onToggle          = { handleAction(MyAction.ToggleMode(it)) },
    showLabels        = true
)
```

---

## Composants layout

### MainScaffold

Scaffold de base avec `innerPadding` géré automatiquement. À utiliser sur tous les écrans principaux.

```kotlin
MainScaffold(navController = navController) { innerPadding ->
    // Contenu de l'écran
}
```

### CenteredBox

Box plein écran centrée, avec padding horizontal configurable.

```kotlin
CenteredBox(horizontalPadding = 24.dp) {
    // Contenu centré
}
```

### CenteredColumn

Colonne centrée pleine largeur.

```kotlin
CenteredColumn(
    horizontalAlignment  = Alignment.CenterHorizontally,
    verticalArrangement  = Arrangement.spacedBy(16.dp)
) {
    // Éléments en colonne
}
```

### Spacers prédéfinis

```kotlin
SmallSpacer()       // 8 dp
MediumSpacer()      // 16 dp
LargeSpacer()       // 24 dp
ExtraLargeSpacer()  // 32 dp
CustomSpacer(48.dp) // personnalisé
```

---

## Thème — Material Design 3

### AppTheme

Wrapper de thème à placer à la racine de l'application :

```kotlin
// MainActivity.kt
setContent {
    AppTheme {
        App()
    }
}
```

`AppTheme` lit `ThemeManager.isDarkTheme()` et applique `LightColorScheme` ou `DarkColorScheme`.

### Palette de couleurs

Les couleurs sont définies dans `ui/core/theme/Color.kt` et mappées dans `Theme.kt`.

**Couleurs Cyna brand :**

| Nom | Valeur | Usage |
|-----|--------|-------|
| `CynaViolet` | `#6C3BF5` | Primaire — accents, boutons |
| `CynaDark` | `#0A0A0F` | Fond sombre |
| `CynaCard` | `#111118` | Surface sombre |
| `CynaVioletLight` | `#7B4FFF` | Primaire en mode sombre |

**Tokens Material mappés :**

```kotlin
// Mode clair
primary              → CynaViolet
onPrimary            → White
background           → blanc cassé teinté violet
surface / card       → blanc pur
muted                → violet très désaturé

// Mode sombre
primary              → CynaVioletLight
background           → CynaDark (#0A0A0F)
card                 → CynaCard (#111118)
secondary            → surface intermédiaire
border               → blanc 10% opacité
```

### Utiliser les couleurs dans les composants

```kotlin
// ✅ Correct — utilise les tokens du thème
Text(
    text  = "Titre",
    color = MaterialTheme.colorScheme.onBackground
)

Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary))

// ❌ À éviter — valeur codée en dur
Text(text = "Titre", color = Color(0xFF6C3BF5))
```

**Exception :** les couleurs de la palette Cyna (`CynaViolet`, `Yellowcyna`…) peuvent être utilisées directement pour des éléments de branding spécifiques (ex. logo, splash screen).

### Gestion du thème

```kotlin
// Basculer entre les modes
ThemeManager.toggleTheme()  // Light → Dark → System → Light

// Forcer un mode
ThemeManager.setTheme(ThemeState.Dark)
ThemeManager.setTheme(ThemeState.Light)
ThemeManager.setTheme(ThemeState.System)  // suit le système
```

---

## Créer un nouveau composant

1. Créer le fichier dans le dossier approprié (`input/` ou `layout/`)
2. Exposer uniquement ce qui est nécessaire via les paramètres
3. Utiliser `MaterialTheme.colorScheme.*` pour toutes les couleurs
4. Fournir des valeurs par défaut sensées pour les paramètres optionnels
5. Ajouter un composable de preview `@Preview` dans le même fichier

```kotlin
// Exemple : composant carte produit
@Composable
fun ProductCard(
    product:  Product,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = product.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text  = "€${product.priceMonthly}/mois",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

---

## Typographie

Définie dans `ui/core/theme/Type.kt` sous `CynaTypography`. Utiliser via :

```kotlin
Text(text = "Titre", style = MaterialTheme.typography.headlineMedium)
Text(text = "Corps", style = MaterialTheme.typography.bodyMedium)
Text(text = "Label", style = MaterialTheme.typography.labelSmall)
```

Les styles disponibles suivent la spécification Material 3 : `displayLarge/Medium/Small`, `headlineLarge/Medium/Small`, `titleLarge/Medium/Small`, `bodyLarge/Medium/Small`, `labelLarge/Medium/Small`.