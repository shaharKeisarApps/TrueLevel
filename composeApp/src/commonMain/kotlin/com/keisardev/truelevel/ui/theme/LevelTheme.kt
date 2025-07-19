package com.keisardev.truelevel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Theme configuration options for TrueLevel application
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class ContrastLevel {
    STANDARD,
    HIGH
}

enum class TextSize {
    STANDARD,
    LARGE,
    COMPACT
}

/**
 * Data class representing the current theme configuration
 */
data class LevelThemeConfig(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val contrastLevel: ContrastLevel = ContrastLevel.STANDARD,
    val textSize: TextSize = TextSize.STANDARD
)

/**
 * Extended color scheme for level-specific colors
 */
data class LevelColorScheme(
    val level: Color,
    val close: Color,
    val notLevel: Color,
    val measurementText: Color,
    val measurementBackground: Color,
    val holdIndicator: Color
)

/**
 * CompositionLocal for accessing level-specific colors
 */
val LocalLevelColors = staticCompositionLocalOf<LevelColorScheme> {
    error("No LevelColorScheme provided")
}

/**
 * CompositionLocal for accessing theme configuration
 */
val LocalLevelThemeConfig = staticCompositionLocalOf<LevelThemeConfig> {
    LevelThemeConfig()
}

/**
 * Main theme composable for TrueLevel application
 * Provides Material Design 3 theming with level-specific extensions
 */
@Composable
fun LevelTheme(
    config: LevelThemeConfig = LevelThemeConfig(),
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (config.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = getColorScheme(isDarkTheme, config.contrastLevel)
    val levelColors = getLevelColors(isDarkTheme, config.contrastLevel)
    val typography = getTypography(config.textSize)
    
    CompositionLocalProvider(
        LocalLevelColors provides levelColors,
        LocalLevelThemeConfig provides config
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

/**
 * Get the appropriate Material Design 3 color scheme
 */
private fun getColorScheme(isDarkTheme: Boolean, contrastLevel: ContrastLevel): ColorScheme {
    return when {
        isDarkTheme && contrastLevel == ContrastLevel.HIGH -> darkColorScheme(
            primary = LevelColors.HighContrastDark.Primary,
            onPrimary = LevelColors.HighContrastDark.OnPrimary,
            primaryContainer = LevelColors.HighContrastDark.PrimaryContainer,
            onPrimaryContainer = LevelColors.HighContrastDark.OnPrimaryContainer,
            secondary = LevelColors.HighContrastDark.Secondary,
            onSecondary = LevelColors.HighContrastDark.OnSecondary,
            secondaryContainer = LevelColors.HighContrastDark.SecondaryContainer,
            onSecondaryContainer = LevelColors.HighContrastDark.OnSecondaryContainer,
            background = LevelColors.HighContrastDark.Background,
            onBackground = LevelColors.HighContrastDark.OnBackground,
            surface = LevelColors.HighContrastDark.Surface,
            onSurface = LevelColors.HighContrastDark.OnSurface,
            surfaceVariant = LevelColors.HighContrastDark.SurfaceVariant,
            onSurfaceVariant = LevelColors.HighContrastDark.OnSurfaceVariant,
            outline = LevelColors.HighContrastDark.Outline,
            outlineVariant = LevelColors.HighContrastDark.OutlineVariant
        )
        
        !isDarkTheme && contrastLevel == ContrastLevel.HIGH -> lightColorScheme(
            primary = LevelColors.HighContrastLight.Primary,
            onPrimary = LevelColors.HighContrastLight.OnPrimary,
            primaryContainer = LevelColors.HighContrastLight.PrimaryContainer,
            onPrimaryContainer = LevelColors.HighContrastLight.OnPrimaryContainer,
            secondary = LevelColors.HighContrastLight.Secondary,
            onSecondary = LevelColors.HighContrastLight.OnSecondary,
            secondaryContainer = LevelColors.HighContrastLight.SecondaryContainer,
            onSecondaryContainer = LevelColors.HighContrastLight.OnSecondaryContainer,
            background = LevelColors.HighContrastLight.Background,
            onBackground = LevelColors.HighContrastLight.OnBackground,
            surface = LevelColors.HighContrastLight.Surface,
            onSurface = LevelColors.HighContrastLight.OnSurface,
            surfaceVariant = LevelColors.HighContrastLight.SurfaceVariant,
            onSurfaceVariant = LevelColors.HighContrastLight.OnSurfaceVariant,
            outline = LevelColors.HighContrastLight.Outline,
            outlineVariant = LevelColors.HighContrastLight.OutlineVariant
        )
        
        isDarkTheme -> darkColorScheme(
            primary = LevelColors.Dark.Primary,
            onPrimary = LevelColors.Dark.OnPrimary,
            primaryContainer = LevelColors.Dark.PrimaryContainer,
            onPrimaryContainer = LevelColors.Dark.OnPrimaryContainer,
            secondary = LevelColors.Dark.Secondary,
            onSecondary = LevelColors.Dark.OnSecondary,
            secondaryContainer = LevelColors.Dark.SecondaryContainer,
            onSecondaryContainer = LevelColors.Dark.OnSecondaryContainer,
            tertiary = LevelColors.Dark.Tertiary,
            onTertiary = LevelColors.Dark.OnTertiary,
            tertiaryContainer = LevelColors.Dark.TertiaryContainer,
            onTertiaryContainer = LevelColors.Dark.OnTertiaryContainer,
            error = LevelColors.Dark.Error,
            onError = LevelColors.Dark.OnError,
            errorContainer = LevelColors.Dark.ErrorContainer,
            onErrorContainer = LevelColors.Dark.OnErrorContainer,
            background = LevelColors.Dark.Background,
            onBackground = LevelColors.Dark.OnBackground,
            surface = LevelColors.Dark.Surface,
            onSurface = LevelColors.Dark.OnSurface,
            surfaceVariant = LevelColors.Dark.SurfaceVariant,
            onSurfaceVariant = LevelColors.Dark.OnSurfaceVariant,
            outline = LevelColors.Dark.Outline,
            outlineVariant = LevelColors.Dark.OutlineVariant,
            scrim = LevelColors.Dark.Scrim,
            inverseSurface = LevelColors.Dark.InverseSurface,
            inverseOnSurface = LevelColors.Dark.InverseOnSurface,
            inversePrimary = LevelColors.Dark.InversePrimary
        )
        
        else -> lightColorScheme(
            primary = LevelColors.Light.Primary,
            onPrimary = LevelColors.Light.OnPrimary,
            primaryContainer = LevelColors.Light.PrimaryContainer,
            onPrimaryContainer = LevelColors.Light.OnPrimaryContainer,
            secondary = LevelColors.Light.Secondary,
            onSecondary = LevelColors.Light.OnSecondary,
            secondaryContainer = LevelColors.Light.SecondaryContainer,
            onSecondaryContainer = LevelColors.Light.OnSecondaryContainer,
            tertiary = LevelColors.Light.Tertiary,
            onTertiary = LevelColors.Light.OnTertiary,
            tertiaryContainer = LevelColors.Light.TertiaryContainer,
            onTertiaryContainer = LevelColors.Light.OnTertiaryContainer,
            error = LevelColors.Light.Error,
            onError = LevelColors.Light.OnError,
            errorContainer = LevelColors.Light.ErrorContainer,
            onErrorContainer = LevelColors.Light.OnErrorContainer,
            background = LevelColors.Light.Background,
            onBackground = LevelColors.Light.OnBackground,
            surface = LevelColors.Light.Surface,
            onSurface = LevelColors.Light.OnSurface,
            surfaceVariant = LevelColors.Light.SurfaceVariant,
            onSurfaceVariant = LevelColors.Light.OnSurfaceVariant,
            outline = LevelColors.Light.Outline,
            outlineVariant = LevelColors.Light.OutlineVariant,
            scrim = LevelColors.Light.Scrim,
            inverseSurface = LevelColors.Light.InverseSurface,
            inverseOnSurface = LevelColors.Light.InverseOnSurface,
            inversePrimary = LevelColors.Light.InversePrimary
        )
    }
}

/**
 * Get level-specific colors based on theme and contrast settings
 */
private fun getLevelColors(isDarkTheme: Boolean, contrastLevel: ContrastLevel): LevelColorScheme {
    return when {
        isDarkTheme && contrastLevel == ContrastLevel.HIGH -> LevelColorScheme(
            level = LevelColors.LevelHighContrast,
            close = LevelColors.CloseHighContrast,
            notLevel = LevelColors.NotLevelHighContrast,
            measurementText = LevelColors.HighContrastDark.MeasurementText,
            measurementBackground = LevelColors.HighContrastDark.MeasurementBackground,
            holdIndicator = LevelColors.HighContrastDark.HoldIndicator
        )
        
        !isDarkTheme && contrastLevel == ContrastLevel.HIGH -> LevelColorScheme(
            level = LevelColors.LevelHighContrast,
            close = LevelColors.CloseHighContrast,
            notLevel = LevelColors.NotLevelHighContrast,
            measurementText = LevelColors.HighContrastLight.MeasurementText,
            measurementBackground = LevelColors.HighContrastLight.MeasurementBackground,
            holdIndicator = LevelColors.HighContrastLight.HoldIndicator
        )
        
        isDarkTheme -> LevelColorScheme(
            level = LevelColors.Level,
            close = LevelColors.Close,
            notLevel = LevelColors.NotLevel,
            measurementText = LevelColors.Dark.MeasurementText,
            measurementBackground = LevelColors.Dark.MeasurementBackground,
            holdIndicator = LevelColors.Dark.HoldIndicator
        )
        
        else -> LevelColorScheme(
            level = LevelColors.Level,
            close = LevelColors.Close,
            notLevel = LevelColors.NotLevel,
            measurementText = LevelColors.Light.MeasurementText,
            measurementBackground = LevelColors.Light.MeasurementBackground,
            holdIndicator = LevelColors.Light.HoldIndicator
        )
    }
}

/**
 * Get typography based on text size preference
 */
private fun getTypography(textSize: TextSize): Typography {
    return when (textSize) {
        TextSize.LARGE -> LevelTypography.Large
        TextSize.COMPACT -> LevelTypography.Compact
        TextSize.STANDARD -> LevelTypography.Default
    }
}

/**
 * Extension function to get level status color
 */
@Composable
fun getLevelStatusColor(status: com.keisardev.truelevel.domain.models.LevelStatus): Color {
    val levelColors = LocalLevelColors.current
    return when (status) {
        com.keisardev.truelevel.domain.models.LevelStatus.LEVEL -> levelColors.level
        com.keisardev.truelevel.domain.models.LevelStatus.CLOSE -> levelColors.close
        com.keisardev.truelevel.domain.models.LevelStatus.NOT_LEVEL -> levelColors.notLevel
    }
}