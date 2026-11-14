package com.aibuilt.pocketmasterai.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val amberLed = Color(0xFFFFB300)
private val greenLed = Color(0xFF4CAF50)
private val midLed = Color(0xFFFF8A65)
private val alarmRed = Color(0xFFFF3D00)
private val pedalboardBlack = Color(0xFF0A0A0A)
private val pedalboardWhite = Color(0xFFE0E0E0)
private val pedalboardSurface = Color(0xFF141414)

private val DarkPedalboardPalette = darkColorScheme(
    primary = amberLed,
    onPrimary = pedalboardBlack,
    secondary = greenLed,
    onSecondary = pedalboardBlack,
    tertiary = midLed,
    background = pedalboardBlack,
    onBackground = pedalboardWhite,
    surface = pedalboardSurface,
    onSurface = pedalboardWhite,
    error = alarmRed,
    onError = pedalboardBlack,
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkPedalboardPalette,
        content = content
    )
}

/** Alias used by the preview so MainActivity can call PedalboardTheme(). */
@Composable
fun PedalboardTheme(content: @Composable () -> Unit) {
    Theme(darkTheme = true, content = content)
}
