package com.cyna.app.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import com.cyna.app.data.local.SessionManager
import com.cyna.app.ui.core.components.ui.AccountSection
import com.cyna.app.ui.core.components.ui.NavTab
import com.cyna.app.ui.screens.auth.LoginScreen
import com.cyna.app.ui.screens.auth.RegisterScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.koinInject

@Serializable
sealed class Destination {
    abstract val route: String
    @Transient
    open val arguments: List<NamedNavArgument> = emptyList()

    @Serializable
    object Login : Destination() {
        override val route = "login"
    }

    @Serializable
    object Register : Destination() {
        override val route = "register"
    }

    @Serializable
    object Profile : Destination() {
        override val route = "profile"
    }

    @Serializable
    object OrdersHistory : Destination() {
        override val route = "orders-history"
    }
}

fun NavGraphBuilder.composable(
    destination: Destination,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) = composable(
    route = destination.route,
    arguments = destination.arguments,
    deepLinks = deepLinks
) { backStackEntry -> content(backStackEntry) }

fun NavController.navigateTo(
    destination: Destination,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) = navigate(route = destination.route, navOptions = navOptions, navigatorExtras = navigatorExtras)

fun NavController.navigateTo(
    destination: Destination,
    builder: NavOptionsBuilder.() -> Unit
) = navigate(route = destination.route, builder = builder)

@Composable
fun NavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val sessionManager: SessionManager = koinInject()
    val user by sessionManager.user.collectAsState()
    val isAuthenticated = user != null

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Destination.OrdersHistory.route else Destination.Login.route,
        modifier = modifier
    ) {
        composable(Destination.Login) {
            LoginScreen(
                onNavigateToRegister = { navController.navigateTo(Destination.Register) },
                onLoginSuccess = {
                    navController.navigateTo(Destination.OrdersHistory) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Destination.Register) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigateTo(Destination.Login) },
                onRegisterSuccess = {
                    navController.navigateTo(Destination.OrdersHistory) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Destination.OrdersHistory) {
            AccountSection(
                initialTab = NavTab.ORDERS
            )
        }
        composable(Destination.Profile) {
            AccountSection(
                initialTab = NavTab.PROFILE
            )
        }
    }
}
