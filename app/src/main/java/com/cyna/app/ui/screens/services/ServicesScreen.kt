package com.cyna.app.ui.screens.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyna.app.ui.screens.services.components.ServiceTelemetryCard
import dev.kindling.compose.KScreen

// ── Screen entry ─────────────────────────────────────────────────────────────

/**
 * Point d'entrée de l'écran des services.
 * Utilise le wrapper [KScreen] pour lier automatiquement le [ServicesViewModel].
 */
@Composable
fun ServicesScreen(navController: NavController) {
    KScreen(
        viewModel = viewModel<ServicesViewModel>(),
        navController = navController
    ) { state, viewModel ->
        // On délègue l'affichage à un composant "stateless" (sans état interne)
        ServicesContent(
            state = state,
            onRetry = viewModel::load
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

/**
 * Contenu principal de l'écran affichant la télémétrie.
 */
@Composable
private fun ServicesContent(
    state: ServicesContracts.UiState,
    onRetry: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when {
            state.loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                    Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Réessayer")
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Télémétrie en direct",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Supervisez l'état de vos solutions de cybersécurité.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(state.services) { service ->
                        ServiceTelemetryCard(service)
                    }
                }
            }
        }
    }
}
