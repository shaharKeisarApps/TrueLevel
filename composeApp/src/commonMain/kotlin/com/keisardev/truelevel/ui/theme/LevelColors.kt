package com.keisardev.truelevel.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Professional color palette for TrueLevel application
 * Provides level status colors and theme colors with accessibility compliance
 */
object LevelColors {
    // Level Status Colors - High contrast for professional use
    val Level = Color(0xFF4CAF50)        // Green - Level (±1 degree)
    val Close = Color(0xFFFF9800)        // Orange - Close to level (1-5 degrees)
    val NotLevel = Color(0xFFF44336)     // Red - Not level (>5 degrees)
    
    // Level Status Colors - High Contrast Mode
    val LevelHighContrast = Color(0xFF00C853)      // Brighter green
    val CloseHighContrast = Color(0xFFFF6F00)      // Brighter orange
    val NotLevelHighContrast = Color(0xFFD50000)   // Brighter red
    
    // Light Theme Colors
    object Light {
        val Primary = Color(0xFF1976D2)
        val OnPrimary = Color(0xFFFFFFFF)
        val PrimaryContainer = Color(0xFFD1E4FF)
        val OnPrimaryContainer = Color(0xFF001D36)
        
        val Secondary = Color(0xFF535F70)
        val OnSecondary = Color(0xFFFFFFFF)
        val SecondaryContainer = Color(0xFFD7E3F7)
        val OnSecondaryContainer = Color(0xFF101C2B)
        
        val Tertiary = Color(0xFF6B5778)
        val OnTertiary = Color(0xFFFFFFFF)
        val TertiaryContainer = Color(0xFFF2DAFF)
        val OnTertiaryContainer = Color(0xFF251431)
        
        val Error = Color(0xFFBA1A1A)
        val OnError = Color(0xFFFFFFFF)
        val ErrorContainer = Color(0xFFFFDAD6)
        val OnErrorContainer = Color(0xFF410002)
        
        val Background = Color(0xFFFEFBFF)
        val OnBackground = Color(0xFF1B1B1F)
        val Surface = Color(0xFFFEFBFF)
        val OnSurface = Color(0xFF1B1B1F)
        val SurfaceVariant = Color(0xFFE1E2EC)
        val OnSurfaceVariant = Color(0xFF44474F)
        
        val Outline = Color(0xFF74777F)
        val OutlineVariant = Color(0xFFC4C7CF)
        val Scrim = Color(0xFF000000)
        val InverseSurface = Color(0xFF303034)
        val InverseOnSurface = Color(0xFFF2F0F4)
        val InversePrimary = Color(0xFF9ECAFF)
        
        // Measurement display specific colors
        val MeasurementText = Color(0xFF1B1B1F)
        val MeasurementBackground = Color(0xFFFFFFFF)
        val HoldIndicator = Color(0xFFFF6F00)
    }
    
    // Dark Theme Colors
    object Dark {
        val Primary = Color(0xFF9ECAFF)
        val OnPrimary = Color(0xFF003258)
        val PrimaryContainer = Color(0xFF00497D)
        val OnPrimaryContainer = Color(0xFFD1E4FF)
        
        val Secondary = Color(0xFFBBC7DB)
        val OnSecondary = Color(0xFF253140)
        val SecondaryContainer = Color(0xFF3B4858)
        val OnSecondaryContainer = Color(0xFFD7E3F7)
        
        val Tertiary = Color(0xFFD6BEE4)
        val OnTertiary = Color(0xFF3A2947)
        val TertiaryContainer = Color(0xFF51405F)
        val OnTertiaryContainer = Color(0xFFF2DAFF)
        
        val Error = Color(0xFFFFB4AB)
        val OnError = Color(0xFF690005)
        val ErrorContainer = Color(0xFF93000A)
        val OnErrorContainer = Color(0xFFFFDAD6)
        
        val Background = Color(0xFF121212)
        val OnBackground = Color(0xFFE3E2E6)
        val Surface = Color(0xFF121212)
        val OnSurface = Color(0xFFE3E2E6)
        val SurfaceVariant = Color(0xFF44474F)
        val OnSurfaceVariant = Color(0xFFC4C7CF)
        
        val Outline = Color(0xFF8E9099)
        val OutlineVariant = Color(0xFF44474F)
        val Scrim = Color(0xFF000000)
        val InverseSurface = Color(0xFFE3E2E6)
        val InverseOnSurface = Color(0xFF303034)
        val InversePrimary = Color(0xFF1976D2)
        
        // Measurement display specific colors
        val MeasurementText = Color(0xFFFFFFFF)
        val MeasurementBackground = Color(0xFF1E1E1E)
        val HoldIndicator = Color(0xFFFFAB00)
    }
    
    // High Contrast Light Theme
    object HighContrastLight {
        val Primary = Color(0xFF000000)
        val OnPrimary = Color(0xFFFFFFFF)
        val PrimaryContainer = Color(0xFF0066CC)
        val OnPrimaryContainer = Color(0xFFFFFFFF)
        
        val Secondary = Color(0xFF000000)
        val OnSecondary = Color(0xFFFFFFFF)
        val SecondaryContainer = Color(0xFF606060)
        val OnSecondaryContainer = Color(0xFFFFFFFF)
        
        val Background = Color(0xFFFFFFFF)
        val OnBackground = Color(0xFF000000)
        val Surface = Color(0xFFFFFFFF)
        val OnSurface = Color(0xFF000000)
        val SurfaceVariant = Color(0xFFF0F0F0)
        val OnSurfaceVariant = Color(0xFF000000)
        
        val Outline = Color(0xFF000000)
        val OutlineVariant = Color(0xFF606060)
        
        // Measurement display specific colors
        val MeasurementText = Color(0xFF000000)
        val MeasurementBackground = Color(0xFFFFFFFF)
        val HoldIndicator = Color(0xFFFF4400)
    }
    
    // High Contrast Dark Theme
    object HighContrastDark {
        val Primary = Color(0xFFFFFFFF)
        val OnPrimary = Color(0xFF000000)
        val PrimaryContainer = Color(0xFF66B3FF)
        val OnPrimaryContainer = Color(0xFF000000)
        
        val Secondary = Color(0xFFFFFFFF)
        val OnSecondary = Color(0xFF000000)
        val SecondaryContainer = Color(0xFFCCCCCC)
        val OnSecondaryContainer = Color(0xFF000000)
        
        val Background = Color(0xFF000000)
        val OnBackground = Color(0xFFFFFFFF)
        val Surface = Color(0xFF000000)
        val OnSurface = Color(0xFFFFFFFF)
        val SurfaceVariant = Color(0xFF1A1A1A)
        val OnSurfaceVariant = Color(0xFFFFFFFF)
        
        val Outline = Color(0xFFFFFFFF)
        val OutlineVariant = Color(0xFFCCCCCC)
        
        // Measurement display specific colors
        val MeasurementText = Color(0xFFFFFFFF)
        val MeasurementBackground = Color(0xFF000000)
        val HoldIndicator = Color(0xFFFFAA00)
    }
}