package com.plcoding.jetpackcomposepokedex.screens.winPokeball

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import com.plcoding.jetpackcomposepokedex.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WinPokeBallScreen(
    gameLevel: Int,
    navController: NavController,
    viewModel: WinPokeballViewModel = hiltNavGraphViewModel()
) {

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colors.background,
            Color.White
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
        tileMode = TileMode.Clamp
    )

    val pokeballSize = remember { mutableStateOf(100.dp) }
    val remainingTime = remember { mutableStateOf(0) }
    val clickCount = remember { mutableStateOf(0) }
    val targetClicks = remember { mutableStateOf(0) }
    val showSnackbar = remember { mutableStateOf(false) }
    val snackbarMessage = remember { mutableStateOf("") }
    val userWon = remember { mutableStateOf(false) }
    var pokeBallsAtStake = 0


    when (gameLevel) {
        1 -> {
            remainingTime.value = 10
            targetClicks.value = 7
            pokeBallsAtStake = 1
        }
        3 -> {
            remainingTime.value = 7
            targetClicks.value = 35
            pokeBallsAtStake = 3
        }
        5 -> {
            remainingTime.value = 5
            targetClicks.value = 40
            pokeBallsAtStake = 5
        }
    }

    rememberCoroutineScope().launch {
        while (remainingTime.value > 0) {
            delay(1000)
            remainingTime.value--
            if (pokeballSize.value > 40.dp) {
                pokeballSize.value -= 1.dp
            }
            if (clickCount.value >= targetClicks.value) {
                snackbarMessage.value = "Congrats you won, Level: $gameLevel!"
                showSnackbar.value = true
                userWon.value = true
                return@launch
            }
        }
        snackbarMessage.value = "You Lost, Level: $gameLevel :/"
        showSnackbar.value = true
        userWon.value = false
    }

    LaunchedEffect(userWon.value) {
        if (!userWon.value) {
            viewModel.subtractPokeBalls(pokeBallsAtStake)
        } else {
            viewModel.add(pokeBallsAtStake + pokeBallsAtStake)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Time Remaining: ${remainingTime.value}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pok__ball_icon_svg),
                contentDescription = "pokeball",
                modifier = Modifier
                    .size(pokeballSize.value)
                    .clickable {
                        pokeballSize.value += 10.dp
                        clickCount.value += 1

                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Clicks: ${clickCount.value} / ${targetClicks.value}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))


        if (showSnackbar.value) {
            LaunchedEffect(showSnackbar) {
                delay(1000)
                showSnackbar.value = false
                navController.popBackStack()
            }

            Snackbar(
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Text(
                    text = snackbarMessage.value,
                    color = Color.White
                )
            }
        }
    }

}
