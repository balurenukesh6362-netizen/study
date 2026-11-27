package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val FastBiteDarkColorScheme = darkColorScheme(
    primary = DarkOrangePrimary,
    onPrimary = Color(0xFF2B0E00),
    primaryContainer = DarkOrangeContainer,
    onPrimaryContainer = Color(0xFFFFDBCA),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF2D1E00),
    secondaryContainer = Color(0xFF533900),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF002114),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFD1FAE5),
    background = DarkCharcoalBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextMuted,
    outline = Color(0xFF473A32),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

private val FastBiteLightColorScheme = lightColorScheme(
    primary = FastBiteOrange,
    onPrimary = CrispWhite,
    primaryContainer = FastBiteOrangeContainer,
    onPrimaryContainer = FastBiteOnOrangeContainer,
    secondary = SaffronAmber,
    onSecondary = CrispWhite,
    secondaryContainer = SaffronAmberContainer,
    onSecondaryContainer = OnSaffronAmberContainer,
    tertiary = FreshEmerald,
    onTertiary = CrispWhite,
    tertiaryContainer = FreshEmeraldContainer,
    onTertiaryContainer = OnFreshEmeraldContainer,
    background = WarmCanvas,
    onBackground = EspressoSlate,
    surface = CrispWhite,
    onSurface = EspressoSlate,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = SlateSecondary,
    outline = SubtleOutline,
    error = CrimsonAlert,
    onError = CrispWhite,
    errorContainer = CrimsonAlertContainer,
    onErrorContainer = OnCrimsonAlertContainer
)

val FastBiteShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun FastBiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FastBiteDarkColorScheme else FastBiteLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FastBiteTypography,
        shapes = FastBiteShapes,
        content = content
    )
}
