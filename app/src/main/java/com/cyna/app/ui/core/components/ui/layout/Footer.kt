package com.cyna.app.ui.core.components.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Footer() {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.surfaceVariant.copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CYNA",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = cs.primary
                )
                Text(
                    "Next-gen cybersecurity solutions for modern enterprises.",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f), horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    "Support",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Help Center", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                Text("Contact Us", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                Text("Terms of Service", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = cs.outlineVariant
        )
        
        Text(
            "© 2026 Cyna Cybersecurity. All rights reserved.",
            style = MaterialTheme.typography.labelSmall,
            color = cs.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
