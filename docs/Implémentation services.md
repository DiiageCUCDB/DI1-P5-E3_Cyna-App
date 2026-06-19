# 📝 Documentation : Implémentation de la vue "Services" (Télémétrie Hybride)

## 🎯 Contexte et Objectif métier
Dans le cadre de l'application cliente Cyna, il est crucial de faire la distinction entre la **facturation** (les abonnements/commandes) et le **provisionnement** (l'utilisation réelle des logiciels, ou "télémétrie").

Cette nouvelle fonctionnalité introduit un onglet **"Services"** affichant la télémétrie en temps réel (appareils protégés, menaces bloquées, statut de connexion).

L'API Backend actuelle (C#) gère la facturation (`GET /user/subscriptions`) mais ne possède pas encore de route de télémétrie. Pour ne pas polluer la base de données de production avec des données factices et pour débloquer le développement Front-End, nous avons opté pour une approche par **"Stubbing" local** dans la couche Data.

---

## 🏗️ Architecture et Décisions Techniques (Le "Stubbing")

L'application récupère les **vrais** abonnements de l'utilisateur via l'API, puis le `Repository` agit comme un intercepteur local pour générer de la fausse télémétrie par-dessus.

**Avantages de cette approche :**
1. **Zéro impact Backend :** Aucune modification n'a été nécessaire sur l'API C#.
2. **UI Pure :** Les écrans et le ViewModel pensent recevoir de vraies données de télémétrie.
3. **Fonctionne en Mock et en Prod :** Si `MOCK_API=true`, on lit les faux abonnements de `UserHandlers`. Si `MOCK_API=false`, on lit les vrais abonnements en base de données. Le stubbing s'applique dans les deux cas.

---

## 📁 Fichiers Modifiés et Ajoutés

### 1. 🧭 Navigation (UI Core)
* **`ui/core/components/ui/BottomNavBar.kt`** : Ajout de l'entrée `NavTab.SERVICES` (icône Computer).
* **`ui/core/components/ui/AccountSection.kt`** : Déclaration de la route `"services"` pointant vers `ServicesScreen`.

### 2. 🧠 Couche Domain (Logique Métier)
* **`domain/model/PurchasedService.kt`** : Modèle pur représentant l'état du service (statut, licences totales, appareils actifs, menaces bloquées).
* **`domain/repository/ServiceRepository.kt`** : Contrat d'interface définissant `getPurchasedServices()`.

### 3. 🌐 Couche Data (Réseau et Enrichissement) - *Le cœur de la feature*
* **`data/dto/SubscriptionDto.kt`** : DTO mappant la réponse existante de `GET /user/subscriptions`.
* **`data/remote/ServiceAPI.kt`** : Appelle la route `/user/subscriptions` de l'API (ou du MockEngine).
* **`data/repository/ServiceRepositoryImpl.kt`** :
    1. Récupère les abonnements via `ServiceAPI`.
    2. Filtre uniquement les abonnements avec le statut "Active".
    3. **Stubbing** : Enrichit les données à la volée avec de la télémétrie aléatoire (calcul du nombre d'appareils selon le plan, probabilité de hors-ligne, génération de menaces).

### 4. 🧹 Couche Mock (Nettoyage)
Puisque nous utilisons la route `/user/subscriptions` (qui était déjà mockée dans `UserHandlers.kt`), nous avons retiré le code mort :
* Suppression de l'ancien `ServiceHandlers.kt` et de `MockPurchasedService` (dans `MockFactories.kt`).
* Nettoyage de l'enregistrement dans `MockInitializer.kt`.

### 5. 📱 Couche UI (Écrans et Composants)
* **`ui/screens/services/ServicesViewModel.kt`** : Utilise le `KViewModel` pour gérer le cycle de vie de la requête (Loading, Success, Error).
* **`ui/screens/services/ServicesScreen.kt`** : Composant "stateless" gérant les différents états d'affichage.
* **`ui/screens/services/components/ServiceTelemetryCard.kt`** : Composant visuel isolé contenant les jauges de progression et les pastilles de statut (ONLINE/OFFLINE).

### 6. 💉 Injection de Dépendances (DI)
* **`di/AppModule.kt`** : Ajout de `ServiceAPI` et `ServiceRepositoryImpl` au graphe Koin.

---

## 🚀 Évolutions futures
Lorsque la véritable API de télémétrie sera disponible côté Backend (ex: `GET /api/telemetry`), il suffira de :
1. Créer le nouveau DTO réseau.
2. Remplacer la logique de génération aléatoire (le Stubbing) dans `ServiceRepositoryImpl.kt` par le second appel réseau.
3. Fusionner les données.
   **Aucune modification de la couche UI ne sera requise.**