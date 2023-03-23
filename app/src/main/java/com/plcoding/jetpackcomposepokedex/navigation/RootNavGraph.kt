package com.plcoding.jetpackcomposepokedex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = MyGraph.ROOT,
        startDestination = MyGraph.HOME
    ) {
        //authNavGraph(navController = navController)
        composable(MyGraph.HOME){

        }

    }
}

object MyGraph{
    const val ROOT = "root_graph"
    const val HOME = "home_graph"
    const val AUTH = "auth_graph"


}