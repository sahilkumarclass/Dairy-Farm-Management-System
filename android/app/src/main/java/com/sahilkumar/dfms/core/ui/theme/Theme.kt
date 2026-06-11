package com.sahilkumar.dfms.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Brand palette, shared with the web app.
val BrandBlue = Color(0xFF00236F)
val BrandGreen = Color(0xFF006E2F)
val BrandRed = Color(0xFFBA1A1A)
val BrandBackground = Color(0xFFF8F9FB)
val BrandSurface = Color(0xFFFFFFFF)
val BrandOnSurface = Color(0xFF191C1E)
val BrandMuted = Color(0xFF4B4E57)
val BrandWarning = Color(0xFFB7791F)

private val LightColors = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    secondary = BrandGreen,
    onSecondary = Color.White,
    tertiary = BrandGreen,
    background = BrandBackground,
    onBackground = BrandOnSurface,
    surface = BrandSurface,
    onSurface = BrandOnSurface,
    surfaceVariant = Color(0xFFEDEEF3),
    onSurfaceVariant = BrandMuted,
    error = BrandRed,
    onError = Color.White,
    outline = Color(0xFFC5C5D3),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAFC6FF),
    secondary = Color(0xFF7DDA8E),
    error = Color(0xFFFFB4AB),
)

private val AppTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.02).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
)

@Composable
fun DairySmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
