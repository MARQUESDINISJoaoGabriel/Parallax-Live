package com.example.parallaxliveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parallaxliveapp.presentation.navigation.AppNavHost
import com.example.parallaxliveapp.presentation.navigation.Screen
import com.example.parallaxliveapp.presentation.theme.ParallaxLiveTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.parallaxlive.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParallaxLiveTheme {
                ParallaxApp()
            }
        }
    }
}

@Composable
fun ParallaxApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if bottom navigation should be visible
    val showBottomNav = currentDestination?.route?.let { route ->
        !Screen.hideBottomNavScreens.any { screen ->
            route == screen.route || route.startsWith(screen.route.substringBefore("{"))
        }
    } ?: true

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar {
                        Screen.bottomNavItems.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.route == screen.route
                            } == true

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painter = painterResource(
                                            id = getIconForScreen(screen)
                                        ),
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(
                                        text = getLabelForScreen(screen)
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
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
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                AppNavHost(
                    navController = navController,
                    startDestination = getStartDestination()
                )
            }
        }
    }
}

/**
 * Returns the appropriate icon resource ID for a given screen.
 */
@Composable
private fun getIconForScreen(screen: Screen): Int {
    return when (screen) {
        Screen.Home -> R.drawable.ic_home
        Screen.StreamHistory -> R.drawable.ic_history
        Screen.Profile -> R.drawable.ic_profile
        else -> R.drawable.ic_home
    }
}

/**
 * Returns the label for a given screen.
 */
@Composable
private fun getLabelForScreen(screen: Screen): String {
    return when (screen) {
        Screen.Home -> stringResource(R.string.home)
        Screen.StreamHistory -> stringResource(R.string.history)
        Screen.Profile -> stringResource(R.string.profile)
        else -> ""
    }
}

/**
 * Determines the start destination based on authentication status.
 */
private fun getStartDestination(): String {
    // TODO: Check if user is logged in
    return Screen.Welcome.route
}