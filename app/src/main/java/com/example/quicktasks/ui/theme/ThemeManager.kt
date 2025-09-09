package com.example.quicktasks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.quicktasks.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Theme manager for handling app theme state
 */
@Composable
fun rememberAppThemeState(
    settingsRepository: SettingsRepository
): AppThemeState {
    val isDarkTheme by settingsRepository.isDarkThemeEnabled().collectAsState(initial = false)
    val systemInDarkTheme = isSystemInDarkTheme()
    
    return remember {
        AppThemeState(
            isDarkTheme = isDarkTheme,
            systemInDarkTheme = systemInDarkTheme,
            shouldUseDarkTheme = isDarkTheme || (!isDarkTheme && systemInDarkTheme)
        )
    }
}

/**
 * State holder for app theme
 */
data class AppThemeState(
    val isDarkTheme: Boolean,
    val systemInDarkTheme: Boolean,
    val shouldUseDarkTheme: Boolean
)

/**
 * Custom color scheme for the app
 */
val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple80,
    onPrimaryContainer = Purple40,
    secondary = PurpleGrey40,
    onSecondary = Color.White,
    secondaryContainer = PurpleGrey80,
    onSecondaryContainer = PurpleGrey40,
    tertiary = Pink40,
    onTertiary = Color.White,
    tertiaryContainer = Pink80,
    onTertiaryContainer = Pink40,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple40,
    primaryContainer = Purple40,
    onPrimaryContainer = Purple80,
    secondary = PurpleGrey80,
    onSecondary = PurpleGrey40,
    secondaryContainer = PurpleGrey40,
    onSecondaryContainer = PurpleGrey80,
    tertiary = Pink80,
    onTertiary = Pink40,
    tertiaryContainer = Pink40,
    onTertiaryContainer = Pink80,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

/**
 * Priority color mapping
 */
fun getPriorityColor(priority: com.example.quicktasks.model.TaskPriority): Color {
    return when (priority) {
        com.example.quicktasks.model.TaskPriority.LOW -> PriorityLow
        com.example.quicktasks.model.TaskPriority.MEDIUM -> PriorityMedium
        com.example.quicktasks.model.TaskPriority.HIGH -> PriorityHigh
        com.example.quicktasks.model.TaskPriority.URGENT -> PriorityUrgent
    }
}

/**
 * Status color mapping
 */
fun getStatusColor(isCompleted: Boolean, isOverdue: Boolean): Color {
    return when {
        isCompleted -> CompletedColor
        isOverdue -> OverdueColor
        else -> PendingColor
    }
}

