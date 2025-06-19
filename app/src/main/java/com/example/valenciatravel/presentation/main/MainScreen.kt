package com.example.valenciatravel.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.valenciatravel.presentation.catalog.CatalogScreen
import com.example.valenciatravel.presentation.feed.FeedScreen
import com.example.valenciatravel.presentation.maps.MapsScreen
import com.example.valenciatravel.presentation.navigation.BottomNavItem
import com.example.valenciatravel.presentation.navigation.Screen
import com.example.valenciatravel.presentation.profile.ProfileScreen

@Composable
fun MainScreen(
    navController: NavController,
    currentRoute: String?,
    onNavigateToLogin: () -> Unit,
    onNavigateToPreferences: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                BottomNavItem.items.forEach { item ->
                    val selected = currentRoute == item.route

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        },
                        selected = selected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentRoute) {
                Screen.Feed.route -> FeedScreen(
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToPlaceDetails = { placeId ->
                        navController.navigate(Screen.PlaceDetails.createRoute(placeId))
                    }
                )
                Screen.Catalog.route -> CatalogScreen(
                    onNavigateToPlaceDetails = { placeId ->
                        navController.navigate(Screen.PlaceDetails.createRoute(placeId))
                    }
                )
                Screen.Maps.route -> MapsScreen(
                    onNavigateToPlaceDetails = { placeId ->
                        navController.navigate(Screen.PlaceDetails.createRoute(placeId))
                    }
                )
                Screen.Profile.route -> ProfileScreen(
                    onNavigateToPreferences = onNavigateToPreferences,
                    onDeleteAccount = onNavigateToLogin,
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    },
                    onNavigateToMain = onNavigateToLogin
                )
            }
        }
    }
}