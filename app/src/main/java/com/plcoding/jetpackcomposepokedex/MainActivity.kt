package com.plcoding.jetpackcomposepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.plcoding.jetpackcomposepokedex.screens.winPokeball.WinPokeBallScreen
import com.plcoding.jetpackcomposepokedex.screens.profile.ProfileScreen
import com.plcoding.jetpackcomposepokedex.screens.pokeDetails.CatchPokemonGameScreen
import com.plcoding.jetpackcomposepokedex.screens.pokeDetails.PokeDetailScreen
import com.plcoding.jetpackcomposepokedex.screens.pokeList.PokeListScreen
import com.plcoding.jetpackcomposepokedex.ui.theme.JetpackComposePokedexTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePokedexTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "pokemon_list") {
                    composable(route = "pokemon_list") {
                        PokeListScreen(navController = navController)
                    }
                    composable(
                        "pokemon_details/{dominantColor}/{pokemonName}",
                        arguments = listOf(
                            navArgument("dominantColor") {
                                type = NavType.IntType
                            },
                            navArgument("pokemonName") {
                                type = NavType.StringType
                            })

                    ) {
                        val dominantColor = remember {
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let { Color(it) ?: Color.White }
                        }
                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }
                        PokeDetailScreen(
                            dominantColor = dominantColor!!,
                            pokeName = pokemonName?.toLowerCase(Locale.ROOT) ?: "",
                            navController = navController
                        )
                    }
                    composable(
                        route = "pokemon_game/{dominantColor}/{pokemonName}",
                        arguments = listOf(
                            navArgument("dominantColor") {
                                type = NavType.IntType
                            },
                            navArgument("pokemonName") {
                                type = NavType.StringType
                            })
                    ) {
                        val dominantColor = remember {
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let { Color(it) ?: Color.White }
                        }
                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }
                        CatchPokemonGameScreen(
                            navController = navController,
                            pokeName = pokemonName?.toLowerCase(Locale.ROOT) ?: "",
                            dominantColor = dominantColor!!
                        )
                    }
                    composable(route = "profile") {
                        ProfileScreen(navController = navController)
                    }
                    composable("pokeball_game/{gameLevel}",
                        arguments = listOf(
                            navArgument("gameLevel") {
                                type = NavType.IntType
                            }
                        )
                    ) {
                        val gameLevel = remember {
                            it.arguments?.getInt("gameLevel")
                        }
                        WinPokeBallScreen(gameLevel = gameLevel!!, navController)
                    }


                }
            }
        }
    }
}

