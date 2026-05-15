# Structure du projet et conventions

## Arborescence

```
Cyna-App/
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/cyna/app/
│           │   ├── Application.kt          # Point d'entrée — initialise Koin
│           │   ├── MainActivity.kt         # Activité principale — hôte Compose
│           │   ├── data/
│           │   │   ├── dto/                # Data Transfer Objects (sérialisation réseau)
│           │   │   ├── remote/             # Clients HTTP (Ktor) et APIs
│           │   │   └── repository/         # Implémentations des repositories
│           │   ├── di/
│           │   │   └── AppModule.kt        # Module Koin — injection de dépendances
│           │   ├── domain/
│           │   │   ├── model/              # Modèles métier purs
│           │   │   ├── repository/         # Interfaces des repositories
│           │   │   └── usecase/            # Cas d'usage (logique métier)
│           │   └── ui/
│           │       ├── core/
│           │       │   ├── Navigation.kt   # Destinations et NavHost
│           │       │   ├── ViewModel.kt    # ViewModel de base générique
│           │       │   ├── components/     # Composants réutilisables
│           │       │   │   ├── Screen.kt   # Wrapper d'écran générique
│           │       │   │   ├── input/      # Boutons, champs, toggles
│           │       │   │   └── layout/     # Scaffolds, colonnes, espacements
│           │       │   └── theme/          # Couleurs, typographie, thème Material
│           │       └── screens/            # Écrans de l'application
│           │           ├── home/
│           │           ├── login/
│           │           └── splash/
│           └── res/
│               ├── drawable/               # Icônes SVG vectorielles
│               ├── mipmap-anydpi/          # Icônes de lancement adaptatives
│               ├── values/                 # Couleurs, chaînes, thèmes XML
│               └── xml/                    # Règles de sauvegarde et extraction
├── build.gradle.kts                        # Plugins Gradle racine
├── gradle/
│   ├── libs.versions.toml                  # Catalogue de versions centralisé
│   └── wrapper/
│       └── gradle-wrapper.properties       # Version Gradle
└── settings.gradle.kts                     # Configuration du projet Gradle
```

---

## Architecture — Clean Architecture à 3 couches

Le projet suit une architecture Clean Architecture adaptée à Android :

```
UI Layer (Compose)
    └── ViewModel (state + events)
            └── Domain Layer (interfaces + modèles)
                    └── Data Layer (Ktor + implémentations)
```

### Couche Data (`data/`)

Responsable de l'accès aux données réseau.

- **`dto/`** — objets de transfert annotés `@Serializable` pour la désérialisation JSON
- **`remote/`** — clients Ktor et classes d'API par ressource
- **`repository/`** — implémentations concrètes des interfaces de domaine

### Couche Domain (`domain/`)

Contient la logique métier pure, sans dépendance Android.

- **`model/`** — data classes métier (pas de `@Serializable`, pas d'Android)
- **`repository/`** — interfaces que la couche data doit implémenter
- **`usecase/`** — cas d'usage encapsulant la logique complexe

### Couche UI (`ui/`)

Composables Compose + ViewModels.

- **`core/`** — infrastructure partagée (navigation, ViewModel de base, composants)
- **`screens/`** — un dossier par écran, contenant l'écran, le ViewModel et les contracts

---

## Conventions de nommage

### Fichiers Kotlin

| Type | Convention | Exemple |
|------|-----------|---------|
| Activité | `PascalCase.kt` | `MainActivity.kt` |
| Composable écran | `PascalCaseScreen.kt` | `LoginScreen.kt` |
| ViewModel | `PascalCaseViewModel.kt` | `LoginViewModel.kt` |
| Repository (interface) | `PascalCaseRepository.kt` | `LoginRepository.kt` |
| Repository (impl) | `PascalCaseRepositoryImpl.kt` | `LoginRepositoryImpl.kt` |
| DTO | `PascalCase.kt` | `Login.kt` |
| Module DI | `PascalCaseModule.kt` | `AppModule.kt` |

### Composants et écrans

Chaque écran est découpé en trois éléments dans le même fichier ou dossier :

```
screens/login/
├── LoginScreen.kt      ← Composable public + composable Content privé
└── LoginViewModel.kt   ← ViewModel + interface LoginContracts
```

Le composable public `LoginScreen` reçoit le `NavController` et délègue à `Screen()`.
Le composable privé `Content` ne reçoit que l'état et un handler d'actions — jamais de dépendances externes.

### Contracts (MVI)

Chaque écran expose ses contrats via une interface imbriquée dans le ViewModel :

```kotlin
interface LoginContracts {
    data class UiState(...)     // État immuable de l'UI
    sealed interface UiAction   // Actions déclenchées par l'utilisateur
    sealed interface Event      // Événements one-shot (navigation, toasts)
}
```

---

## Injection de dépendances — Koin

Toutes les dépendances sont déclarées dans `di/AppModule.kt` et initialisées dans `Application.kt`.

```kotlin
val appModule = module {
    single<HttpClient> { createHttpClient(baseUrl = API_URL) }
    single { LoginAPI(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
}
```

Règles :
- `single` pour les singletons (clients HTTP, repositories)
- `factory` pour les instances recréées à chaque injection
- Ne jamais injecter directement dans un `@Composable` — passer par le ViewModel
- Les ViewModels récupèrent leurs dépendances via `by inject()` (délégué Koin)

---

## Gradle et dépendances

Le projet utilise le **Version Catalog** (`gradle/libs.versions.toml`) pour centraliser toutes les versions.

```toml
[versions]
ktor = "2.3.7"
koin = "3.5.0"

[libraries]
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }

[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

Ne jamais écrire de version en dur dans `build.gradle.kts` — toujours référencer le catalog.

---

## Variables d'environnement et configuration

Les constantes sensibles (URLs de base, clés API) sont définies dans `di/AppModule.kt` :

```kotlin
private const val API_URL = "http://your-api.com/api/"
```

Pour la production, ces valeurs doivent être externalisées via `BuildConfig` ou un fichier `.properties` non versionné. Ne jamais committer de clés API ou de tokens dans le dépôt.

---

## Thème et design

Le thème est centralisé dans `ui/core/theme/` :

- **`Color.kt`** — palette de couleurs nommées (Cyna brand + Material)
- **`Theme.kt`** — schémas `lightColorScheme` / `darkColorScheme` + `AppTheme`
- **`Type.kt`** — typographie Material 3
- **`ThemeManager.kt`** — état global du thème (clair / sombre / système)

Les composants ne doivent jamais coder de couleurs en dur — toujours utiliser `MaterialTheme.colorScheme.*`.