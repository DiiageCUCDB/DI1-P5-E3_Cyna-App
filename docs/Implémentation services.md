📝 Documentation : Implémentation de la vue "Services" (Télémétrie Mockée)

🎯 Contexte et Objectif métier

Dans le cadre de l'application cliente Cyna, il est crucial de faire la distinction entre la facturation (les abonnements/commandes) et le provisionnement (l'utilisation réelle des logiciels).

Cette nouvelle fonctionnalité introduit un onglet "Services". Son but est d'afficher la télémétrie en temps réel des solutions de cybersécurité achetées par le client (ex: nombre d'appareils protégés par un EDR, alertes bloquées, statut de connexion), de manière totalement indépendante de la vue "Orders".

Dans l'attente du développement de l'API Backend, cette fonctionnalité a été entièrement implémentée selon notre Clean Architecture (Domain, Data, UI) en utilisant notre MockEngine Ktor interne pour simuler les réponses réseau.

🏗️ Architecture et Fichiers Modifiés

L'implémentation a touché toutes les couches de l'application. Voici le détail de l'organisation :

1. 🧭 Navigation (UI Core)

Ajout d'un nouvel onglet central dans la navigation de l'utilisateur connecté.

ui/core/components/ui/BottomNavBar.kt : Ajout de l'entrée NavTab.SERVICES avec l'icône Computer.

ui/core/components/ui/AccountSection.kt : Mise à jour du sous-routeur (innerNav) pour déclarer la route "services" et afficher le ServicesScreen.

2. 🧠 Couche Domain (Logique Métier)

Définition des modèles purs, sans dépendance réseau ou Android.

domain/model/PurchasedService.kt : Création du modèle représentant la télémétrie (Statut, Licences totales, Appareils actifs, Menaces bloquées). Utilisation d'une enum class ServiceStatus (ONLINE, WARNING, OFFLINE).

domain/repository/ServiceRepository.kt : Contrat de l'interface définissant la méthode getPurchasedServices().

3. 🌐 Couche Data (Réseau et Mapping)

Gestion de la requête HTTP et traduction du JSON vers notre modèle métier.

data/dto/PurchasedServiceDto.kt : Objet de transfert sérialisable (@Serializable) reflétant exactement la structure JSON attendue.

data/remote/ServiceAPI.kt : Client Ktor spécifique. Exécute la requête GET /user/services.

data/repository/ServiceRepositoryImpl.kt : Implémentation du repository. Orchestre l'appel à l'API et s'occupe du mapping (DTO -> Domain), incluant le parsing des dates et la conversion des statuts string en Enum.

4. 🎭 Couche Mock (Le faux Back-End)

Génération de données factices interceptées par notre client HTTP.

mock/factories/MockFactories.kt : Création du MockPurchasedService (DTO) et d'une factory makePurchasedService() générant des données cohérentes (noms de logiciels Cyna, calcul de fausses licences, statuts aléatoires).

mock/handlers/ServiceHandlers.kt : Ajout d'un handler qui intercepte la route GET /user/services et retourne une liste générée par la factory.

mock/MockInitializer.kt : Enregistrement du nouveau serviceHandlers dans le MockRegistry.

5. 📱 Couche UI (Écrans et Composants)

Affichage basé sur Jetpack Compose avec séparation des responsabilités.

ui/screens/services/ServicesViewModel.kt : Utilisation du KViewModel pour gérer le ServicesContracts.UiState. Fait l'appel au repository et gère l'état asynchrone (Loading, Success, Error).

ui/screens/services/ServicesScreen.kt : Composant "stateless" (bête). Utilise KScreen pour s'interfacer avec le ViewModel et gère l'affichage des différents états (Loader, Message d'erreur, ou Liste).

ui/screens/services/components/ServiceTelemetryCard.kt : Extraction du composant visuel de la carte (Jauges de progression, pastilles de statut) dans son propre fichier pour garder l'écran principal lisible.

6. 💉 Injection de Dépendances (DI)

di/AppModule.kt : Déclaration du ServiceAPI et du ServiceRepositoryImpl  dans le graphe de dépendances Koin.

🚀 Prochaines Étapes prévues

Mock "Intelligent" : Modifier le MockEngine pour que la génération de la télémétrie soit liée aux vraies fausses commandes (OrderHistory) de l'utilisateur (ex: générer la télémétrie EDR uniquement si une commande EDR a été trouvée pour ce client).

Intégration Backend : Lorsque la route C# GET user/services sera disponible, il suffira de désactiver le MockInitializer. Aucun changement ne sera nécessaire dans les couches Domain, Data ou UI grâce au design pattern Repository.