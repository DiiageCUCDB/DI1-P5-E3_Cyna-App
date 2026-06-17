package com.cyna.app.ui.screens.services.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyna.app.domain.model.PurchasedService
import com.cyna.app.domain.model.ServiceStatus

@Composable
fun ServiceTelemetryCard(service: PurchasedService) {
    val isOffline = service.status == ServiceStatus.OFFLINE
    val usagePercent = if (service.totalLicenses > 0) (service.activeUsage.toFloat() / service.totalLicenses) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // En-tête
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = service.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = service.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }

                val (icon, color, text) = when (service.status) {
                    ServiceStatus.ONLINE -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50), "En ligne")
                    ServiceStatus.WARNING -> Triple(Icons.Default.Warning, Color(0xFFFF9800), "Alerte")
                    else -> Triple(Icons.Default.Error, MaterialTheme.colorScheme.error, "Hors ligne")
                }

                AssistChip(
                    onClick = { },
                    label = { Text(text) },
                    leadingIcon = { Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = color.copy(alpha = 0.1f))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Jauge
            Text(
                text = "Appareils protégés : ${service.activeUsage} / ${service.totalLicenses}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            LinearProgressIndicator(
                progress = { usagePercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(top = 4.dp, bottom = 12.dp),
                color = if (usagePercent > 0.9f) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            // Pied de carte
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Menaces bloquées : ${if (isOffline) "--" else service.threatsBlocked}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (service.threatsBlocked > 0 && !isOffline) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sync : ${service.lastSyncTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}