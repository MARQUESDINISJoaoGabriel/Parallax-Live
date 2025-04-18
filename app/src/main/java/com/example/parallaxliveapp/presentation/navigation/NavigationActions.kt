package com.example.parallaxliveapp.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions

/**
 * Helper class that facilitates navigation throughout the app.
 * Provides functions to navigate to different screens while handling the back stack appropriately.
 */
class NavigationActions(private val navController: NavController) {

    /**
     * Navigate to the welcome screen, clearing the back stack.
     * This is typically used for sign-out operations.
     */
    fun navigateToWelcome() {
        navController.navigate(Screen.Welcome.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    /**
     * Navigate to the login screen.
     */
    fun navigateToLogin() {
        navController.navigate(Screen.Login.route)
    }

    /**
     * Navigate to the registration screen.
     */
    fun navigateToRegister() {
        navController.navigate(Screen.Register.route)
    }

    /**
     * Navigate to the password recovery screen.
     */
    fun navigateToPasswordRecovery() {
        navController.navigate(Screen.PasswordRecovery.route)
    }

    /**
     * Navigate to the home screen, clearing the back stack.
     * This is typically used after successful login or registration.
     */
    fun navigateToHome() {
        navController.navigate(Screen.Home.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    /**
     * Navigate to the profile screen.
     * If coming from bottom navigation, clear back stack up to the start destination.
     */
    fun navigateToProfile() {
        navController.navigate(Screen.Profile.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    /**
     * Navigate to the stream configuration screen.
     */
    fun navigateToStreamConfig() {
        navController.navigate(Screen.StreamConfig.route)
    }

    /**
     * Navigate to the live stream screen with the specified stream ID.
     */
    fun navigateToLiveStream(streamId: String) {
        navController.navigate(Screen.LiveStream.createRoute(streamId))
    }

    /**
     * Navigate to the stream history screen.
     * If coming from bottom navigation, clear back stack up to the start destination.
     */
    fun navigateToStreamHistory() {
        navController.navigate(Screen.StreamHistory.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    /**
     * Navigate up (back) in the navigation stack.
     * This is equivalent to the user pressing the back button.
     */
    fun navigateBack() {
        navController.popBackStack()
    }
}