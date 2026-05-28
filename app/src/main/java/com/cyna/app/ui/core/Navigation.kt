// ── Navigation additions for Navigation.kt ───────────────────────────────────
//
// 1. Add new Destinations:
//
//    object Profile : Destination(route = "profile")
//    object Catalog : Destination(route = "catalog")
//
// 2. Register in NavHost:
//
//    composable(Destination.Profile) { ProfileScreen(onNavigateToCatalog = { navController.navigate(Destination.Catalog) }, onNavigateToOrders = { /* orders screen */ }) }
//    composable(Destination.Catalog) { CatalogScreen() }
//
// Full updated NavHost block:

package com.cyna.app.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cyna.app.ui.screens.ComponentShowcaseScreen
import com.cyna.app.ui.screens.catalog.CatalogScreen
import com.cyna.app.ui.screens.profile.ProfileScreen

sealed class Destination(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    object Splash   : Destination(route = "splash")
    object Home     : Destination(route = "home")
    object Login    : Destination(route = "login")
    object Profile  : Destination(route = "profile")
    object Catalog  : Destination(route = "catalog")
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

fun NavController.navigate(
    destination: Destination,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) = navigate(route = destination.route, navOptions = navOptions, navigatorExtras = navigatorExtras)

@Composable
fun NavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destination.Catalog.route, // change start as needed
        modifier = modifier
    ) {
        composable(Destination.Profile) {
            ProfileScreen(
                onNavigateToCatalog = { navController.navigate(Destination.Catalog) },
                onNavigateToOrders = { /* TODO: OrdersScreen */ }
            )
        }

        composable(Destination.Catalog) {
            CatalogScreen()
        }
    }
}