package com.example.parallaxliveapp.presentation.navigation

/**
 * Screen destinations for navigation.
 */
sealed class Screen(val route: String) {
    // Auth screens
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object PasswordRecovery : Screen("password_recovery")

    // Main app screens
    object Home : Screen("home")
    object Profile : Screen("profile")
    object StreamConfig : Screen("stream_config")
    object LiveStream : Screen("live_stream/{streamId}") {
        fun createRoute(streamId: String) = "live_stream/$streamId"
    }
    object StreamHistory : Screen("stream_history")

    companion object {
        /**
         * List of screens that are part of the bottom navigation
         */
        val bottomNavItems = listOf(
            Home,
            StreamHistory,
            Profile
        )

        /**
         * List of screens where the bottom navigation should be hidden
         */
        val hideBottomNavScreens = listOf(
            Welcome,
            Login,
            Register,
            PasswordRecovery,
            LiveStream,
            StreamConfig
        )
    }
}