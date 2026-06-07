package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SleekIndigoLight,
    onPrimary = Color.White,
    primaryContainer = SleekIndigo,
    onPrimaryContainer = Color.White,
    secondary = SleekIndigoLight,
    onSecondary = Color.White,
    tertiary = SunsetGoldLight,
    background = SleekDarkSlate,
    onBackground = Color.White,
    surface = SleekCardDark,
    onSurface = Color.White,
    surfaceVariant = SleekSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Color(0xFFEF4444)
)

private val LightColorScheme = lightColorScheme(
    primary = SleekIndigo,
    onPrimary = Color.White,
    primaryContainer = SleekIndigoBg,
    onPrimaryContainer = SleekIndigoText,
    secondary = SleekIndigoLight,
    onSecondary = Color.White,
    tertiary = SunsetGold,
    background = SleekSlateBg,
    onBackground = SleekSlateText,
    surface = Color.White,
    onSurface = SleekSlateText,
    surfaceVariant = SleekSlateLight,
    onSurfaceVariant = SleekSlateSub,
    error = Color(0xFFDC2626)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Priority to Sleek Interface Theme!
    content: @Composable () -> Unit,
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
