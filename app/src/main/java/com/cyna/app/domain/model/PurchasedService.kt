package com.cyna.app.domain.model

/**
 * Représente l'état en direct d'un logiciel de cybersécurité acheté par le client.
 * Contient la télémétrie (utilisation des licences, menaces bloquées) remontée par l'API.
 *
 * @property id Identifiant unique du service provisionné.
 * @property name Nom commercial du produit (ex: "Shield EDR Pro").
 * @property category Catégorie technique (ex: "EDR", "SIEM", "MDM").
 * @property status Statut de connexion du service (Online, Warning, Offline).
 * @property activeUsage Nombre d'appareils/utilisateurs actuellement connectés ou protégés.
 * @property totalLicenses Nombre total de licences achetées (limite du contrat).
 * @property threatsBlocked Nombre d'alertes ou de menaces bloquées récemment.
 * @property lastSyncTime Date et heure de la dernière remontée de télémétrie.
 */
data class PurchasedService(
    val id: String,
    val name: String,
    val category: String,
    val status: ServiceStatus,
    val activeUsage: Int,
    val totalLicenses: Int,
    val threatsBlocked: Int,
    val lastSyncTime: String
)

/** État de santé du service en temps réel. */
enum class ServiceStatus {
    ONLINE, WARNING, OFFLINE, UNKNOWN
}