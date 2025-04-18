package com.example.parallaxliveapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parallaxliveapp.presentation.ui.auth.login.LoginScreen
import com.example.parallaxliveapp.presentation.ui.auth.password.PasswordRecoveryScreen
import com.example.parallaxliveapp.presentation.ui.auth.register.RegisterScreen
import com.example.parallaxliveapp.presentation.ui.auth.welcome.WelcomeScreen
import com.example.parallaxliveapp.presentation.ui.history.StreamHistoryScreen
import com.example.parallaxliveapp.presentation.ui.home.HomeScreen
import com.example.parallaxliveapp.presentation.ui.profile.ProfileScreen
import com.example.parallaxliveapp.presentation.ui.stream.config.StreamConfigScreen
import com.example.parallaxliveapp.presentation.ui.stream.live.LiveStreamScreen

/**
 * Main navigation component for the app.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Welcome.route
) {
    // Create actions to navigate between screens
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication screens
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = navigationActions::navigateToLogin,
                onNavigateToRegister = navigationActions::navigateToRegister,
                onNavigateToHome = navigationActions::navigateToHome
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = navigationActions::navigateToHome,
                onNavigateToRegister = navigationActions::navigateToRegister,
                onNavigateToPasswordRecovery = navigationActions::navigateToPasswordRecovery,
                onNavigateBack = navigationActions::navigateBack
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = navigationActions::navigateToHome,
                onNavigateToLogin = navigationActions::navigateToLogin,
                onNavigateBack = navigationActions::navigateBack
            )
        }

        composable(Screen.PasswordRecovery.route) {
            PasswordRecoveryScreen(
                onPasswordResetSent = navigationActions::navigateToLogin,
                onNavigateBack = navigationActions::navigateBack
            )
        }

        // Main app screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToStreamConfig = navigationActions::navigateToStreamConfig,
                onNavigateToStreamDetails = navigationActions::navigateToLiveStream
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = navigationActions::navigateToWelcome
            )
        }

        composable(Screen.StreamConfig.route) {
            StreamConfigScreen(
                onStreamCreated = navigationActions::navigateToLiveStream,
                onNavigateBack = navigationActions::navigateBack
            )
        }

        composable(
            route = Screen.LiveStream.route,
            arguments = listOf(
                navArgument("streamId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val streamId = backStackEntry.arguments?.getString("streamId") ?: ""
            LiveStreamScreen(
                streamId = streamId,
                onStreamEnded = navigationActions::navigateToHome,
                onNavigateBack = navigationActions::navigateBack
            )
        }

        composable(Screen.StreamHistory.route) {
            StreamHistoryScreen(
                onStreamSelected = navigationActions::navigateToLiveStream
            )
        }
    }
}