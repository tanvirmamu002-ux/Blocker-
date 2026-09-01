package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppColors = staticCompositionLocalOf { LightAppColorPalette }

object AppTheme {
    val colors: AppColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkAppColorPalette.primary,
    onPrimary = DarkAppColorPalette.surface,
    primaryContainer = DarkAppColorPalette.primaryDim,
    onPrimaryContainer = DarkAppColorPalette.primaryBright,
    secondary = DarkAppColorPalette.secondary,
    onSecondary = DarkAppColorPalette.surface,
    secondaryContainer = DarkAppColorPalette.secondaryDim,
    onSecondaryContainer = DarkAppColorPalette.secondary,
    tertiary = DarkAppColorPalette.alert,
    onTertiary = DarkAppColorPalette.textPrimary,
    background = DarkAppColorPalette.background,
    onBackground = DarkAppColorPalette.textPrimary,
    surface = DarkAppColorPalette.surface,
    onSurface = DarkAppColorPalette.textPrimary,
    surfaceVariant = DarkAppColorPalette.surfaceElevated,
    onSurfaceVariant = DarkAppColorPalette.textSecondary,
    outline = DarkAppColorPalette.border,
    outlineVariant = DarkAppColorPalette.borderLight
)

private val LightColorScheme = lightColorScheme(
    primary = LightAppColorPalette.primary,
    onPrimary = LightAppColorPalette.surface,
    primaryContainer = LightAppColorPalette.primaryDim,
    onPrimaryContainer = LightAppColorPalette.primary,
    secondary = LightAppColorPalette.secondary,
    onSecondary = LightAppColorPalette.surface,
    secondaryContainer = LightAppColorPalette.secondaryDim,
    onSecondaryContainer = LightAppColorPalette.secondary,
    tertiary = LightAppColorPalette.alert,
    onTertiary = LightAppColorPalette.surface,
    background = LightAppColorPalette.background,
    onBackground = LightAppColorPalette.textPrimary,
    surface = LightAppColorPalette.surface,
    onSurface = LightAppColorPalette.textPrimary,
    surfaceVariant = LightAppColorPalette.surfaceElevated,
    onSurfaceVariant = LightAppColorPalette.textSecondary,
    outline = LightAppColorPalette.border,
    outlineVariant = LightAppColorPalette.borderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkAppColorPalette else LightAppColorPalette
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = palette.background.toArgb()
                window.navigationBarColor = palette.background.toArgb()
                
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalAppColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
