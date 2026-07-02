package com.revrank.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = Void,
    surface = Surface,
    primary = MatrixGreen,
    onPrimary = Void,
    secondary = MatrixDim,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Border,
    error = Danger,
    onError = Void
)

@Composable
fun RevRankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = RevRankTypography,
        shapes = RevRankShapes,
        content = content
    )
}
