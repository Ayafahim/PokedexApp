package com.plcoding.jetpackcomposepokedex.screens.favorites

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun FavoritesScreen(navController: NavController) {
    FallingBall()
}


@Composable
fun FallingBall(
    animDuration: Int = 1000,
    animDelay: Int = 0,
    ballSize: Dp = 48.dp,
    ballColor: Color = Color.Red,
) {
    val density = LocalDensity.current
    var animationPlayed by remember { mutableStateOf(false) }
    val curYPos = animateFloatAsState(
        targetValue = if (animationPlayed) {
            1f
        } else 0f,
        animationSpec = tween(animDuration, animDelay)
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    val pixelSize = with(density) { ballSize.toPx() }
    val maxHeight =   200.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ballSize)
    ) {
        Box(
            modifier = Modifier
                .size(ballSize)
                .clip(CircleShape)
                .background(ballColor)
        )
    }
}

@Preview
@Composable
fun preview () {
    FavoritesScreen(rememberNavController())
}






