package com.plcoding.jetpackcomposepokedex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.plcoding.jetpackcomposepokedex.screens.BottomNavigationScreen

@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = MyGraph.HOME,
        startDestination = BottomNavigationScreen.Home.route
    ) {
        composable(route = BottomNavigationScreen.Home.route) {

        }
        composable(route = BottomNavigationScreen.Favorites.route) {

        }

    }
}