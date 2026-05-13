package com.example.madecommerce.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0E5CAD),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD5E4FF),
    onPrimaryContainer = Color(0xFF001C3A),
    secondary = Color(0xFF5B5F97),
    secondaryContainer = Color(0xFFE2E0FF),
    tertiary = Color(0xFF146C5A),
    tertiaryContainer = Color(0xFF9DF2DD),
    background = Color(0xFFF7F9FC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EEF5),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8C8FF),
    onPrimary = Color(0xFF00315F),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFD5E4FF),
    secondary = Color(0xFFC3C4FF),
    secondaryContainer = Color(0xFF43487D),
    tertiary = Color(0xFF81D5C2),
    tertiaryContainer = Color(0xFF005143),
    background = Color(0xFF101418),
    surface = Color(0xFF171C20),
    surfaceVariant = Color(0xFF263039),
)

@Composable
fun MADEcommerceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
