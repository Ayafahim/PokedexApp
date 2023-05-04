package com.plcoding.jetpackcomposepokedex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFFC46500),
    background = Color(0xFF101010),
    onBackground = Color.White,
    surface = Color(0xFF303030),
    onSurface = Color.White,
    secondary =  Color(0xFFFAFBFC)
)

private val LightColorPalette = lightColors(
    primary = Color(0xFFFF9800),
    background = Color(0xFF7FC0F3),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    secondary = Color.Black
)

@Composable
fun JetpackComposePokedexTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}