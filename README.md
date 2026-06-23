# Documentation Cyna Android — Index

> Documentation technique complète de l'application Android Cyna.  
> Architecture : Clean Architecture · MVI · Jetpack Compose · Ktor · Koin

---

## 📁 Structure de la documentation

```
docs/
├── 01_Structure_et_conventions.md   ← Arborescence, règles de nommage, principes généraux
├── 02_Architecture.md               ← Clean Architecture, flux de données, diagrammes
│
├── 03_Couche_reseau.md              ← Ktor, DTOs, repositories, gestion d'erreurs
├── 04_Authentification.md           ← JWT par cookie, SessionManager, flux login/logout
├── 05_Injection_dependances.md      ← Koin, AppModule, déclarations
│
├── 06_ViewModel_et_etat.md          ← MVI, KViewModel, fetchData, UiState
├── 07_Navigation.md                 ← NavHost, Destination, NavController
│
├── 08_Composants_UI_et_theme.md     ← Composants réutilisables, thème Material 3
├── 09_i18n.md                       ← Internationalisation, strings.xml
│
├── 10_Kindling.md                   ← Bibliothèque Kindling (core/utils/android/compose)
│
├── 11_Mock_et_tests.md              ← MockEngine, MockRegistry, MockFactories
└── 12_mes_services.md               ← surveillance des services achetés par l'utilisateur
```

---

## 🗺️ Vue d'ensemble de l'application

```
┌─────────────────────────────────────────────────────────────┐
│                        Application                          │
│                                                             │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────────┐   │
│  │  Auth Flow   │   │ Orders Flow  │   │  Profile Flow  │   │
│  │              │   │              │   │                │   │
│  │ LoginScreen  │   │ OrderHistory │   │ ProfileScreen  │   │
│  │ RegisterScr. │   │ Screen       │   │                │   │
│  └──────────────┘   └──────────────┘   └────────────────┘   │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                    Kindling UI                       │   │
│  │  KButton · KInput · KBadge · Skeleton · Toaster...   │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────────┐   │
│  │  Koin (DI)   │   │  Ktor (HTTP) │   │  SessionMgr    │   │
│  │  AppModule   │   │  AuthAPI     │   │  SharedPrefs   │   │
│  │              │   │  UserAPI     │   │  CookieStorage │   │
│  └──────────────┘   └──────────────┘   └────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Mock Layer (MOCK_API=true)             │    │
│  │   MockEngine · MockRegistry · MockFactories         │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Stack technique

| Domaine | Technologie | Version |
|---------|------------|---------|
| UI | Jetpack Compose + Material 3 | BOM 2026.05.01 |
| Navigation | Navigation Compose | 2.9.8 |
| DI | Koin | 4.2.1 |
| Réseau | Ktor Client | 3.5.0 |
| Sérialisation | kotlinx.serialization | 1.11.0 |
| UI Library | Kindling | 1.0.9 |
| Images | Coil Compose | 2.6.0 |
| Compilateur | Kotlin | 2.3.21 |
| SDK min | Android 29 (Android 10) | — |
| SDK cible | Android 36 | — |

---

## 🔗 Navigation rapide par thème

**Démarrer sur le projet**
→ [Structure et conventions](01_Structure_et_conventions.md) · [Architecture](02_Architecture.md)

**Travailler sur une fonctionnalité**
→ [Réseau](03_Couche_reseau.md) · [ViewModel](06_ViewModel_et_etat.md) · [Navigation](07_Navigation.md)

**Développement local / mock**
→ [Mock et tests](11_Mock_et_tests.md) · [Auth](04_Authentification.md)

**UI**
→ [Composants & thème](08_Composants_UI_et_theme.md) · [Kindling](10_Kindling.md) · [i18n](09_i18n.md)