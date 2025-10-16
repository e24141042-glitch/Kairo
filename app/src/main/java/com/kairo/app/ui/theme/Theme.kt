package com.kairo.app.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = KairoDarkPrimary,
    onPrimary = KairoDarkOnPrimary,
    primaryContainer = KairoDarkPrimaryContainer,
    onPrimaryContainer = KairoDarkOnPrimaryContainer,
    secondary = KairoDarkSecondary,
    onSecondary = KairoDarkOnSecondary,
    secondaryContainer = KairoDarkSecondaryContainer,
    onSecondaryContainer = KairoDarkOnSecondaryContainer,
    tertiary = KairoDarkTertiary,
    onTertiary = KairoDarkOnTertiary,
    tertiaryContainer = KairoDarkTertiaryContainer,
    onTertiaryContainer = KairoDarkOnTertiaryContainer,
    error = KairoDarkError,
    onError = KairoDarkOnError,
    errorContainer = KairoDarkErrorContainer,
    onErrorContainer = KairoDarkOnErrorContainer,
    background = KairoDarkBackground,
    onBackground = KairoDarkOnBackground,
    surface = KairoDarkSurface,
    onSurface = KairoDarkOnSurface,
    surfaceVariant = KairoDarkSurfaceVariant,
    onSurfaceVariant = KairoDarkOnSurfaceVariant,
    outline = KairoDarkOutline,
    outlineVariant = KairoDarkOutlineVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = KairoPrimary,
    onPrimary = KairoOnPrimary,
    primaryContainer = KairoPrimaryContainer,
    onPrimaryContainer = KairoOnPrimaryContainer,
    secondary = KairoSecondary,
    onSecondary = KairoOnSecondary,
    secondaryContainer = KairoSecondaryContainer,
    onSecondaryContainer = KairoOnSecondaryContainer,
    tertiary = KairoTertiary,
    onTertiary = KairoOnTertiary,
    tertiaryContainer = KairoTertiaryContainer,
    onTertiaryContainer = KairoOnTertiaryContainer,
    error = KairoError,
    onError = KairoOnError,
    errorContainer = KairoErrorContainer,
    onErrorContainer = KairoOnErrorContainer,
    background = KairoBackground,
    onBackground = KairoOnBackground,
    surface = KairoSurface,
    onSurface = KairoOnSurface,
    surfaceVariant = KairoSurfaceVariant,
    onSurfaceVariant = KairoOnSurfaceVariant,
    outline = KairoOutline,
    outlineVariant = KairoOutlineVariant,
)

@Composable
fun KairoTheme(
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
