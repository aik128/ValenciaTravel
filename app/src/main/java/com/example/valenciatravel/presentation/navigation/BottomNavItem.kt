package com.example.valenciatravel.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Feed : BottomNavItem(
        route = Screen.Feed.route,
        title = "Лента",
        icon = Icons.Default.Home
    )

    object Catalog : BottomNavItem(
        route = Screen.Catalog.route,
        title = "Каталог мест",
        icon = Icons.Default.Explore
    )

    object Maps : BottomNavItem(
        route = Screen.Maps.route,
        title = "Карты",
        icon = Icons.Default.Map
    )

    object Profile : BottomNavItem(
        route = Screen.Profile.route,
        title = "Профиль",
        icon = Icons.Default.AccountCircle
    )

    companion object {
        val items = listOf(Feed, Catalog, Maps, Profile)
    }
}