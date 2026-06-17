package com.cyna.app.data.repository

import com.cyna.app.data.remote.ServiceAPI
import com.cyna.app.domain.model.PurchasedService
import com.cyna.app.domain.model.ServiceStatus
import com.cyna.app.domain.repository.ServiceRepository

/**
 * Implémentation concrète du repository.
 * Son rôle est d'orchestrer l'appel réseau et de "nettoyer" la donnée (Mapping DTO -> Domain).
 */
internal class ServiceRepositoryImpl(
    private val serviceAPI: ServiceAPI
) : ServiceRepository {

    override suspend fun getPurchasedServices(): List<PurchasedService> =
        serviceAPI.getPurchasedServices().map { dto ->
            PurchasedService(
                id = dto.id,
                name = dto.name,
                category = dto.category,
                // On transforme la string du JSON en Enum Kotlin sécurisée
                status = try {
                    ServiceStatus.valueOf(dto.status.uppercase())
                } catch (e: Exception) {
                    ServiceStatus.UNKNOWN
                },
                activeUsage = dto.activeUsage,
                totalLicenses = dto.totalLicenses,
                threatsBlocked = dto.threatsBlocked,
                // On nettoie la date ISO (ex: 2026-06-13T15:30:00Z -> 2026-06-13 15:30)
                lastSyncTime = dto.lastSyncTime.replace("T", " ").substringBefore("Z")
            )
        }
}