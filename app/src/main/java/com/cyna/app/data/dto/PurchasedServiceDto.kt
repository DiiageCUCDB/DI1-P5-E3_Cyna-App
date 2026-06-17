package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object représentant le JSON exact renvoyé par l'API pour un service.
 * @Serializable permet à Ktor de parser la réponse réseau automatiquement.
 */
@Serializable
internal data class PurchasedServiceDto(
    val id: String,
    val name: String,
    val category: String,
    val status: String,
    val activeUsage: Int,
    val totalLicenses: Int,
    val threatsBlocked: Int,
    val lastSyncTime: String
)