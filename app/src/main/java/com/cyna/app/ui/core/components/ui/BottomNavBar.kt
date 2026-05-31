package com.cyna.app.ui.core.components.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class NavTab { ORDERS, PROFILE }

private data class NavItem(
    val tab: NavTab?,          // null = logout action
    val label: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false
)

private val navItems = listOf(
    NavItem(NavTab.ORDERS,  "Orders",   Icons.Default.Receipt),
    NavItem(NavTab.PROFILE, "Profile",  Icons.Default.Person),
    NavItem(null,           "Sign out", Icons.Default.ExitToApp, isDestructive = true)
)

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme

    NavigationBar(
        modifier = modifier,
        containerColor = cs.surface,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            val isActive = item.tab == currentTab
            val tint = when {
                item.isDestructive -> cs.error
                isActive           -> cs.primary
                else               -> cs.onSurfaceVariant
            }

            NavigationBarItem(
                selected = isActive,
                onClick = {
                    if (item.tab != null) onTabSelected(item.tab)
                    else onLogout()
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = tint
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = tint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = if (item.isDestructive)
                        cs.error.copy(alpha = 0.1f)
                    else
                        cs.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}