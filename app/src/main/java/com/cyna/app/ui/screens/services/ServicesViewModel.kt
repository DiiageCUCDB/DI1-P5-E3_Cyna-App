package com.cyna.app.ui.screens.services

import android.app.Application
import com.cyna.app.domain.model.PurchasedService
import com.cyna.app.domain.repository.ServiceRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

/**
 * Contrats définissant l'état (State) de l'écran des services.
 */
interface ServicesContracts {
    data class UiState(
        val services: List<PurchasedService> = emptyList(),
        val loading: Boolean = true,
        val error: String? = null
    )
}

/**
 * ViewModel responsable de la récupération de la télémétrie des services.
 * Utilise [KViewModel] pour gérer facilement l'état asynchrone.
 */
class ServicesViewModel(application: Application) :
    KViewModel<ServicesContracts.UiState>(ServicesContracts.UiState(), application) {

    // Injection de dépendance via Koin hors de Compose
    private val serviceRepository: ServiceRepository by inject()

    init {
        load()
    }

    /**
     * Charge les services depuis le repository.
     * Utilise `fetchData` (fonction utilitaire de KViewModel) pour gérer automatiquement
     * les coroutines et les blocs onSuccess/onFailure.
     */
    fun load() {
        updateState { copy(loading = true, error = null) }
        fetchData(
            source = { serviceRepository.getPurchasedServices() },
            onResult = {
                onSuccess { services ->
                    updateState { copy(services = services, loading = false) }
                }
                onFailure { e ->
                    updateState { copy(loading = false, error = e.message ?: "Erreur réseau inconnue") }
                }
            }
        )
    }
}