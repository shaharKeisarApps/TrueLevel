package com.keisardev.truelevel.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography scale optimized for measurement display hierarchy
 * Emphasizes readability and clear visual hierarchy for professional use
 */
object LevelTypography {
    
    /**
     * Standard typography for normal viewing conditions
     */
    val Default = Typography(
        // Large measurement display - Primary angle readings
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = (-0.25).sp
        ),
        
        // Medium measurement display - Secondary readings
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        
        // Small measurement display - Tertiary information
        displaySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        
        // Mode titles and section headers
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        
        // Status text and descriptions
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.1.sp
        ),
        
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        
        // Body text for general content
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        
        // Labels for buttons and UI elements
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp
        )
    )
    
    /**
     * Large text typography for accessibility
     * Increased font sizes for users with visual impairments
     */
    val Large = Typography(
        displayLarge = Default.displayLarge.copy(fontSize = 64.sp, lineHeight = 72.sp),
        displayMedium = Default.displayMedium.copy(fontSize = 48.sp, lineHeight = 56.sp),
        displaySmall = Default.displaySmall.copy(fontSize = 36.sp, lineHeight = 44.sp),
        
        headlineLarge = Default.headlineLarge.copy(fontSize = 40.sp, lineHeight = 48.sp),
        headlineMedium = Default.headlineMedium.copy(fontSize = 32.sp, lineHeight = 40.sp),
        headlineSmall = Default.headlineSmall.copy(fontSize = 28.sp, lineHeight = 36.sp),
        
        titleLarge = Default.titleLarge.copy(fontSize = 24.sp, lineHeight = 32.sp),
        titleMedium = Default.titleMedium.copy(fontSize = 20.sp, lineHeight = 28.sp),
        titleSmall = Default.titleSmall.copy(fontSize = 18.sp, lineHeight = 24.sp),
        
        bodyLarge = Default.bodyLarge.copy(fontSize = 20.sp, lineHeight = 28.sp),
        bodyMedium = Default.bodyMedium.copy(fontSize = 18.sp, lineHeight = 24.sp),
        bodySmall = Default.bodySmall.copy(fontSize = 16.sp, lineHeight = 22.sp),
        
        labelLarge = Default.labelLarge.copy(fontSize = 18.sp, lineHeight = 24.sp),
        labelMedium = Default.labelMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
        labelSmall = Default.labelSmall.copy(fontSize = 14.sp, lineHeight = 20.sp)
    )
    
    /**
     * Compact typography for small screens
     * Optimized for space-constrained environments
     */
    val Compact = Typography(
        displayLarge = Default.displayLarge.copy(fontSize = 40.sp, lineHeight = 48.sp),
        displayMedium = Default.displayMedium.copy(fontSize = 32.sp, lineHeight = 40.sp),
        displaySmall = Default.displaySmall.copy(fontSize = 24.sp, lineHeight = 32.sp),
        
        headlineLarge = Default.headlineLarge.copy(fontSize = 28.sp, lineHeight = 36.sp),
        headlineMedium = Default.headlineMedium.copy(fontSize = 20.sp, lineHeight = 28.sp),
        headlineSmall = Default.headlineSmall.copy(fontSize = 18.sp, lineHeight = 24.sp),
        
        titleLarge = Default.titleLarge.copy(fontSize = 16.sp, lineHeight = 22.sp),
        titleMedium = Default.titleMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
        titleSmall = Default.titleSmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
        
        bodyLarge = Default.bodyLarge.copy(fontSize = 14.sp, lineHeight = 20.sp),
        bodyMedium = Default.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
        bodySmall = Default.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
        
        labelLarge = Default.labelLarge.copy(fontSize = 12.sp, lineHeight = 18.sp),
        labelMedium = Default.labelMedium.copy(fontSize = 10.sp, lineHeight = 14.sp),
        labelSmall = Default.labelSmall.copy(fontSize = 8.sp, lineHeight = 12.sp)
    )
}