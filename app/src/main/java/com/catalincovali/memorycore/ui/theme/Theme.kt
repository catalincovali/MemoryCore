package com.catalincovali.memorycore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary      = LightPrimary,
    background   = LightBackground,
    surface      = LightSurface,

)

private val DarkColorScheme = darkColorScheme(
    primary      = DarkPrimary,
    background   = DarkBackground,
    surface      = DarkSurface,

)

@Composable
fun MemoryCoreTheme(
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
