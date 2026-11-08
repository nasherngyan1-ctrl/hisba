package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = SlateGray,
    onPrimaryContainer = PureWhite,
    secondary = PureWhite,
    onSecondary = PureBlack,
    secondaryContainer = SlateGray,
    onSecondaryContainer = PureWhite,
    error = Color(0xFFEF4444),
    onError = PureWhite,
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFCA5A5),
    background = PureBlack,
    onBackground = PureWhite,
    surface = DarkCharcoal,
    onSurface = PureWhite,
    surfaceVariant = SlateGray,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = DarkCharcoal,
    onPrimary = PureWhite,
    primaryContainer = LightGray,
    onPrimaryContainer = PureBlack,
    secondary = DarkCharcoal,
    onSecondary = PureWhite,
    secondaryContainer = LightGray,
    onSecondaryContainer = PureBlack,
    error = NeutralMutedRed,
    onError = PureWhite,
    errorContainer = NeutralMutedRedLight,
    onErrorContainer = NeutralMutedRed,
    background = PureWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    surfaceVariant = OffWhite,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
