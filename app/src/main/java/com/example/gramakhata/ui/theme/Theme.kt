package com.example.gramakhata.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = BrownAccent,
    background = MudBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun GramaKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
