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

/**
 * Sealed class representing all possible destinations in the application.
 *
 * This class defines the navigation graph structure and provides type-safe routing
 * for the entire application. Each destination contains a route and optional arguments.
 *
 * @property route The unique string identifier for this destination
 * @property arguments List of navigation arguments required for this destination
 *
 * @see NavController
 * @see NavGraphBuilder
 */
sealed class Destination(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {

    /**
     * Splash screen destination - the initial screen shown when the app starts.
     */
    object Splash : Destination(route = "splash")

    /**
     * Home screen destination - the main screen after successful authentication.
     */
    object Home : Destination(route = "home")

    /**
     * Login screen destination - authentication screen for user login.
     */
    object Login : Destination(route = "login")
}

/**
 * Extension function for [NavGraphBuilder] to add composable destinations in a type-safe way.
 *
 * This function simplifies the process of adding composable screens to the navigation graph
 * by using [Destination] objects instead of raw strings.
 *
 * @param destination The [Destination] to add to the navigation graph
 * @param deepLinks List of deep links associated with this destination
 * @param content The composable content to display for this destination
 *
 * @sample
 * // Usage in NavGraphBuilder
 * composable(Destination.Home) { backStackEntry ->
 *     HomeScreen(navController)
 * }
 */
fun NavGraphBuilder.composable(
    destination: Destination,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) = composable(
    route = destination.route,
    arguments = destination.arguments,
    deepLinks = deepLinks
) { backStackEntry ->
    content(backStackEntry)
}

/**
 * Extension function for [NavController] to navigate to destinations in a type-safe way.
 *
 * This function provides a cleaner API for navigation by using [Destination] objects
 * instead of raw route strings, reducing the risk of typos and making refactoring easier.
 *
 * @param destination The [Destination] to navigate to
 * @param navOptions Navigation options for this navigation action
 * @param navigatorExtras Extra navigation options provided by the Navigator
 *
 * @sample
 * // Usage with NavController
 * navController.navigate(Destination.Home)
 *
 * @sample
 * // Usage with navigation options
 * navController.navigate(
 *     destination = Destination.Login,
 *     navOptions = NavOptions.Builder()
 *         .setPopUpTo(Destination.Splash.route, inclusive = true)
 *         .build()
 * )
 */
fun NavController.navigate(
    destination: Destination,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) = navigate(
    route = destination.route,
    navOptions = navOptions,
    navigatorExtras = navigatorExtras
)

/**
 * Main navigation host composable that defines the application's navigation graph.
 *
 * This function sets up the navigation structure with [Destination.Splash] as the
 * starting point and defines all available screens in the application.
 *
 * @param navController The [NavHostController] that manages navigation between screens
 * @param modifier Optional [Modifier] for styling the navigation host
 *
 * @see NavHost
 * @see Destination
 *
 * @sample
 * // Usage in MainActivity or root composable
 * val navController = rememberNavController()
 * NavHost(navController = navController)
 */
@Composable
fun NavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Splash.route,
        modifier = modifier
    ) {
        // Authentication Flow
        composable(Destination.Splash) { ComponentShowcaseScreen() }
    }
}