package com.example.valenciatravel.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Preferences : Screen("preferences")
    object Feed : Screen("feed")
    object Catalog : Screen("catalog")
    object Maps : Screen("maps")
    object Profile : Screen("profile")
    object PlaceDetails : Screen("place_details/{placeId}") {
        fun createRoute(placeId: Long) = "place_details/$placeId"
    }
    object Favorites : Screen("favorites")
}