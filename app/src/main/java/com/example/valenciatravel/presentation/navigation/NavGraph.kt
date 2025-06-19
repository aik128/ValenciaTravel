package com.example.valenciatravel.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.valenciatravel.presentation.auth.login.LoginScreen
import com.example.valenciatravel.presentation.auth.register.RegisterScreen
import com.example.valenciatravel.presentation.details.PlaceDetailsScreen
import com.example.valenciatravel.presentation.favorites.FavoritesScreen
import com.example.valenciatravel.presentation.main.MainScreen
import com.example.valenciatravel.presentation.preferences.PreferencesScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToGuest = {
                    navController.navigate(Screen.Catalog.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToPreferences = {
                    navController.navigate(Screen.Preferences.route)
                }
            )
        }

        composable(Screen.Preferences.route) {
            PreferencesScreen(
                onNavigateToMain = {
                    if (navController.previousBackStackEntry?.destination?.route == Screen.Register.route) {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Preferences.route) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Screen.Feed.route) {
            MainScreen(
                navController = navController,
                currentRoute = currentRoute,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToPreferences = {
                    navController.navigate(Screen.Preferences.route)
                }
            )
        }

        composable(Screen.Catalog.route) {
            MainScreen(
                navController = navController,
                currentRoute = currentRoute,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToPreferences = {
                    navController.navigate(Screen.Preferences.route)
                }
            )
        }

        composable(Screen.Maps.route) {
            MainScreen(
                navController = navController,
                currentRoute = currentRoute,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToPreferences = {
                    navController.navigate(Screen.Preferences.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            MainScreen(
                navController = navController,
                currentRoute = currentRoute,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToPreferences = {
                    navController.navigate(Screen.Preferences.route)
                }
            )
        }

        composable(Screen.PlaceDetails.route) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")?.toLongOrNull() ?: 0L
            PlaceDetailsScreen(
                placeId = placeId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateToPlaceDetails = { placeId ->
                    navController.navigate(Screen.PlaceDetails.createRoute(placeId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}