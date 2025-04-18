package com.example.parallaxliveapp.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = ParallaxPrimary,
    onPrimary = ParallaxOnPrimary,
    primaryContainer = ParallaxPrimaryLight,
    onPrimaryContainer = ParallaxPrimaryDark,
    secondary = ParallaxSecondary,
    onSecondary = ParallaxOnSecondary,
    secondaryContainer = ParallaxSecondaryLight,
    onSecondaryContainer = ParallaxSecondaryDark,
    tertiary = ParallaxAccent,
    onTertiary = ParallaxOnPrimary,
    tertiaryContainer = ParallaxAccentLight,
    onTertiaryContainer = ParallaxAccentDark,
    error = ParallaxError,
    onError = ParallaxOnSecondary,
    errorContainer = ParallaxSecondaryLight,
    onErrorContainer = ParallaxSecondaryDark,
    background = ParallaxBackground,
    onBackground = ParallaxOnBackground,
    surface = ParallaxSurface,
    onSurface = ParallaxOnSurface,
    surfaceVariant = ParallaxBackground,
    onSurfaceVariant = ParallaxOnBackground,
    outline = ParallaxDivider
)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = ParallaxPrimaryLight,
    onPrimary = ParallaxPrimary,
    primaryContainer = ParallaxPrimaryDark,
    onPrimaryContainer = ParallaxPrimaryLight,
    secondary = ParallaxSecondaryLight,
    onSecondary = ParallaxSecondary,
    secondaryContainer = ParallaxSecondaryDark,
    onSecondaryContainer = ParallaxSecondaryLight,
    tertiary = ParallaxAccentLight,
    onTertiary = ParallaxAccent,
    tertiaryContainer = ParallaxAccentDark,
    onTertiaryContainer = ParallaxAccentLight,
    error = ParallaxSecondaryLight,
    onError = ParallaxSecondary,
    errorContainer = ParallaxSecondaryDark,
    onErrorContainer = ParallaxSecondaryLight,
    background = ParallaxBackgroundDark,
    onBackground = ParallaxOnBackgroundDark,
    surface = ParallaxSurfaceDark,
    onSurface = ParallaxOnSurfaceDark,
    surfaceVariant = ParallaxBackgroundDark,
    onSurfaceVariant = ParallaxOnBackgroundDark,
    outline = ParallaxDividerDark
)

/**
 * Parallax Live theme.
 *
 * @param darkTheme Whether to use dark theme
 * @param dynamicColor Whether to use dynamic color (Android 12+)
 */
@Composable
fun ParallaxLiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        typography = ParallaxTypography,
        content = content
    )
}