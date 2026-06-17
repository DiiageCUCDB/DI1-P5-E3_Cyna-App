package com.cyna.app.domain.repository

import com.cyna.app.domain.model.PurchasedService

/**
 * Contrat pour l'accès aux données des services provisionnés.
 * La couche UI appellera cette interface, sans savoir si les données
 * viennent d'un Mock, de Ktor, ou d'une base locale.
 */
interface ServiceRepository {
    suspend fun getPurchasedServices(): List<PurchasedService>
}