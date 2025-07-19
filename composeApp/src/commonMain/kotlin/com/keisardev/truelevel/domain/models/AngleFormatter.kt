package com.keisardev.truelevel.domain.models

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

/**
 * Utility class for formatting angle measurements with precision
 */
object AngleFormatter {
    
    /**
     * Formats angle to 0.1-degree accuracy as required by specifications
     */
    fun formatToTenthDegree(angle: Double): String {
        return AngleUtils.formatAngle(angle, 1)
    }
    
    /**
     * Formats angle with specified decimal places
     */
    fun formatAngle(angle: Double, decimalPlaces: Int = 1): String {
        return AngleUtils.formatAngle(angle, decimalPlaces)
    }
    
    /**
     * Formats angle with degree symbol
     */
    fun formatWithDegreeSymbol(angle: Double, decimalPlaces: Int = 1): String {
        return "${formatAngle(angle, decimalPlaces)}°"
    }
    
    /**
     * Formats angle with sign prefix for display
     */
    fun formatWithSign(angle: Double, decimalPlaces: Int = 1): String {
        val formatted = formatAngle(abs(angle), decimalPlaces)
        val sign = when {
            angle > 0 -> "+"
            angle < 0 -> "-"
            else -> ""
        }
        return "$sign$formatted°"
    }
    
    /**
     * Formats both X and Y angles for dual-axis display
     */
    fun formatDualAxis(angleX: Double, angleY: Double, decimalPlaces: Int = 1): String {
        val xFormatted = formatWithDegreeSymbol(angleX, decimalPlaces)
        val yFormatted = formatWithDegreeSymbol(angleY, decimalPlaces)
        return "X: $xFormatted, Y: $yFormatted"
    }
    
    /**
     * Formats angle for accessibility (screen reader friendly)
     */
    fun formatForAccessibility(angle: Double, decimalPlaces: Int = 1): String {
        val formatted = formatAngle(abs(angle), decimalPlaces)
        val direction = when {
            angle > 0 -> "positive"
            angle < 0 -> "negative"
            else -> ""
        }
        val degrees = if (abs(angle) == 1.0) "degree" else "degrees"
        
        return if (direction.isNotEmpty()) {
            "$direction $formatted $degrees"
        } else {
            "$formatted $degrees"
        }
    }
    
    /**
     * Formats level status with angle information
     */
    fun formatStatusWithAngle(levelStatus: LevelStatus, maxAngle: Double): String {
        val angleStr = formatWithDegreeSymbol(maxAngle)
        return "${levelStatus.displayName} ($angleStr)"
    }
    
    /**
     * Checks if angle should be displayed as zero (within rounding threshold)
     */
    fun isEffectivelyZero(angle: Double, threshold: Double = 0.05): Boolean {
        return abs(angle) < threshold
    }
    
    /**
     * Formats angle with conditional zero display
     */
    fun formatWithZeroThreshold(angle: Double, decimalPlaces: Int = 1, threshold: Double = 0.05): String {
        return if (isEffectivelyZero(angle, threshold)) {
            formatAngle(0.0, decimalPlaces)
        } else {
            formatAngle(angle, decimalPlaces)
        }
    }
    
    /**
     * Formats measurement for professional display with status color indication
     */
    fun formatMeasurementDisplay(measurement: LevelMeasurement): MeasurementDisplay {
        return MeasurementDisplay(
            primaryAngle = formatWithDegreeSymbol(measurement.angleX),
            secondaryAngle = formatWithDegreeSymbol(measurement.angleY),
            statusText = measurement.levelStatus.displayName,
            statusColor = measurement.levelStatus.colorHex,
            dualAxisText = formatDualAxis(measurement.angleX, measurement.angleY),
            accessibilityText = "${formatForAccessibility(measurement.angleX)} X-axis, ${formatForAccessibility(measurement.angleY)} Y-axis, ${measurement.levelStatus.description}"
        )
    }
}

/**
 * Data class for formatted measurement display information
 */
data class MeasurementDisplay(
    val primaryAngle: String,
    val secondaryAngle: String,
    val statusText: String,
    val statusColor: String,
    val dualAxisText: String,
    val accessibilityText: String
)