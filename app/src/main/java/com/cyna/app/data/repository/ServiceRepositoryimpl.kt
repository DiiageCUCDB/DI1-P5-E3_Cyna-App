package com.cyna.app.data.repository

import com.cyna.app.data.remote.ServiceAPI
import com.cyna.app.domain.model.PurchasedService
import com.cyna.app.domain.model.ServiceStatus
import com.cyna.app.domain.repository.ServiceRepository
import kotlin.random.Random

/**
 * Récupère les abonnements de l'API et les enrichit localement avec de la
 * fausse télémétrie pour l'affichage (Separation of Concerns).
 */
internal class ServiceRepositoryImpl(
    private val serviceAPI: ServiceAPI
) : ServiceRepository {

    override suspend fun getPurchasedServices(): List<PurchasedService> {
        // 1. Appel réseau (récupère les abonnements réels ou mockés)
        val subscriptions = serviceAPI.getUserSubscriptions()

        // On ne garde que les abonnements qui sont actifs (on ignore "Cancelled" ou "Expired")
        val activeSubscriptions = subscriptions.filter { it.status.equals("Active", ignoreCase = true) }

        // 2. Enrichissement local (Fausse Télémétrie)
        return activeSubscriptions.map { sub ->

            // --- DEBUT DU GENERATEUR DE FAUSSE TELEMETRIE ---
            val isOffline = Random.nextFloat() > 0.85f // 15% de chances d'être hors ligne
            val totalLicences = extractLicenceCount(sub.planName)
            val activeDevices = if (isOffline) 0 else Random.nextInt(1, totalLicences)
            val status = if (isOffline) ServiceStatus.OFFLINE else ServiceStatus.ONLINE
            val threats = Random.nextInt(0, 1500)
            // --- FIN DU GENERATEUR ---

            // 3. Mapping vers le modèle métier de l'écran
            PurchasedService(
                id = sub.id.toString(),
                name = sub.productName,
                category = guessCategoryFromName(sub.productName),
                status = status,
                activeUsage = activeDevices,
                totalLicenses = totalLicences,
                threatsBlocked = threats,
                lastSyncTime = "À l'instant"
            )
        }
    }

    /**
     * Helper : Déduit la catégorie de cybersécurité en fonction du nom du produit Cyna.
     */
    private fun guessCategoryFromName(productName: String): String {
        val upperName = productName.uppercase()
        return when {
            upperName.contains("EDR") -> "EDR"
            upperName.contains("SIEM") -> "SIEM"
            upperName.contains("MDM") -> "MDM"
            upperName.contains("SOC") -> "SOC"
            upperName.contains("XDR") -> "XDR"
            upperName.contains("ZERO TRUST") -> "Zero Trust"
            else -> "Cybersécurité"
        }
    }

    /**
     * Helper : Simule un nombre de licences total basé sur le nom du plan tarifaire.
     */
    private fun extractLicenceCount(planName: String): Int {
        val upperPlan = planName.uppercase()
        return when {
            upperPlan.contains("ENTERPRISE") -> 500
            upperPlan.contains("PRO") -> 100
            upperPlan.contains("STARTER") -> 25
            else -> 50
        }
    }
}