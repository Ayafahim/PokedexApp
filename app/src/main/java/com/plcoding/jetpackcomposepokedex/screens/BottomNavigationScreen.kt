package com.plcoding.jetpackcomposepokedex.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home: BottomNavigationScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Favorites: BottomNavigationScreen(
        route = "favorites",
        title = "Favorites",
        icon = Icons.Default.Favorite
    )

}