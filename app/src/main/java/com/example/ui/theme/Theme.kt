package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenLight,
    onPrimary = DeepForestGreen,
    primaryContainer = DeepForestGreen,
    onPrimaryContainer = SoftMint,
    secondary = SoftMint,
    onSecondary = DeepForestGreen,
    secondaryContainer = Color(0xFF1E382E),
    onSecondaryContainer = SoftMint,
    tertiary = MutedSage,
    onTertiary = Charcoal,
    background = Color(0xFF121B16),
    onBackground = WarmIvory,
    surface = Color(0xFF1A2620),
    onSurface = WarmIvory,
    surfaceVariant = Color(0xFF22332B),
    onSurfaceVariant = WarmIvory,
    outline = MutedSage,
    error = ErrorRed,
    errorContainer = Color(0xFF450A0A),
    onError = PureWhite,
    onErrorContainer = ErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = PureWhite,
    primaryContainer = SoftMint,
    onPrimaryContainer = DeepForestGreen,
    secondary = DeepForestGreen,
    onSecondary = PureWhite,
    secondaryContainer = SoftMintDarker,
    onSecondaryContainer = DeepForestGreen,
    tertiary = MutedSage,
    onTertiary = DeepForestGreen,
    background = WarmIvory,
    onBackground = Charcoal,
    surface = PureWhite,
    onSurface = Charcoal,
    surfaceVariant = SoftMint,
    onSurfaceVariant = DeepForestGreen,
    outline = CardBorder,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onError = PureWhite,
    onErrorContainer = ErrorRed
)

@Composable
fun FoodShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Preserve custom nature-inspired branding
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
