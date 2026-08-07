package com.chumian.browser.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B6ABF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E3FF),
    onPrimaryContainer = Color(0xFF141B4A),
    secondary = Color(0xFF5C5D72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE2E0F9),
    onSecondaryContainer = Color(0xFF191A2C),
    tertiary = Color(0xFF7A536D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD8EE),
    onTertiaryContainer = Color(0xFF2F1128),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC6C5D0),
    inverseSurface = Color(0xFF303034),
    inverseOnSurface = Color(0xFFF3EFF4),
    inversePrimary = Color(0xFFC2C7FF),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF5B6ABF),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC2C7FF),
    onPrimary = Color(0xFF2A3060),
    primaryContainer = Color(0xFF424C8F),
    onPrimaryContainer = Color(0xFFE0E3FF),
    secondary = Color(0xFFC6C4DD),
    onSecondary = Color(0xFF2E2F42),
    secondaryContainer = Color(0xFF444659),
    onSecondaryContainer = Color(0xFFE2E0F9),
    tertiary = Color(0xFFEAB8D9),
    onTertiary = Color(0xFF4A253E),
    tertiaryContainer = Color(0xFF623C55),
    onTertiaryContainer = Color(0xFFFFD8EE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1B1B1F),
    onBackground = Color(0xFFE5E1E6),
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE5E1E6),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C5D0),
    outline = Color(0xFF90909A),
    outlineVariant = Color(0xFF45464F),
    inverseSurface = Color(0xFFE5E1E6),
    inverseOnSurface = Color(0xFF303034),
    inversePrimary = Color(0xFF5B6ABF),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFC2C7FF),
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun ChumianBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
