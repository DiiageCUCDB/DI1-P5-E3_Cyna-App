package com.cyna.app.ui.core.components.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cyna.app.domain.repository.AuthRepository
import com.cyna.app.ui.screens.ordershistory.OrderHistoryScreen
import com.cyna.app.ui.screens.profile.ProfileScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AccountSection(
    initialTab: NavTab = NavTab.ORDERS,
) {
    val innerNav = rememberNavController()
    val authRepository: AuthRepository = koinInject()
    val scope = rememberCoroutineScope()

    // Derive the selected tab directly from the inner nav back stack —
    // this is the single source of truth, no separate mutableStateOf needed.
    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val currentTab = when (backStackEntry?.destination?.route) {
        "profile" -> NavTab.PROFILE
        else      -> NavTab.ORDERS
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    val route = when (tab) {
                        NavTab.ORDERS  -> "orders"
                        NavTab.PROFILE -> "profile"
                    }
                    innerNav.navigate(route) {
                        popUpTo(innerNav.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    scope.launch {
                        runCatching { authRepository.logout() }
                            .onSuccess {
                                innerNav.navigate("orders") {
                                    popUpTo(innerNav.graph.startDestinationId) { inclusive = true }
                                }
                            }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = innerNav,
            startDestination = when (initialTab) {
                NavTab.ORDERS  -> "orders"
                NavTab.PROFILE -> "profile"
            }
        ) {
            composable("orders") { OrderHistoryScreen(innerNav) }
            composable("profile") { ProfileScreen(innerNav) }
        }
    }
}