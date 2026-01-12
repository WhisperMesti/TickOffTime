package com.example.tickofftime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = Mint, //main colour, top bar
    surface = Lightmint, //blocks, lists, tasks
    background = Cream, //background
    secondary = Lightmint, //additional to primary
    onSurface = Black, //rame
    onPrimary = Black //text
)

private val DarkColorPalette = darkColors(
    primary = Dark,
    surface = Blue,
    background = Greenblue,
    secondary = Blue,
    onSurface = White,
    onPrimary = White
)

//preparing proper theme
@Composable
fun TickOffTimeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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