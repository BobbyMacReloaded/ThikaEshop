package com.example.thikaeshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
object EShopColors {
    // Primary Brand Colors
    val Orange = Color(0xFFFF6B35)
    val OrangeLight = Color(0xFFFF8C42)
    val OrangeDark = Color(0xFFE85D04)
    val Gold = Color(0xFFFFD700)
    val GoldLight = Color(0xFFFFE44D)

    // Background Colors
    val DarkBg = Color(0xFF0F0F1A)
    val DarkCard = Color(0xFF1A1A2E)
    val DarkElevated = Color(0xFF252542)
    val LightBg = Color(0xFF1A1A2E)  // Added this for compatibility

    // Glassmorphism Colors
    val GlassWhite = Color.White.copy(alpha = 0.08f)
    val GlassWhiteStrong = Color.White.copy(alpha = 0.12f)
    val GlassBorder = Color.White.copy(alpha = 0.15f)
    val White10 = Color.White.copy(alpha = 0.08f)
    val White20 = Color.White.copy(alpha = 0.12f)
    val White30 = Color.White.copy(alpha = 0.15f)
    val White40 = Color.White.copy(alpha = 0.4f)
    val White50 = Color.White.copy(alpha = 0.5f)
    val White60 = Color.White.copy(alpha = 0.6f)
    val White80 = Color.White.copy(alpha = 0.8f)
    val White = Color.White
    val PrimaryGradient = listOf(Orange, OrangeLight)
    val GoldGradient = listOf(Gold, GoldLight)
    val DarkGradient = listOf(DarkBg, DarkCard)
    val GlassGradient = listOf(GlassWhite, GlassWhiteStrong)
    // Status Colors
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFF44336)
    val Warning = Color(0xFFFFC107)
}

@Composable
fun ThikaEshopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}