package com.keisardev.truelevel.domain.models

import kotlin.math.*

/**
 * Utility functions for angle calculations and conversions
 */
object AngleUtils {
    
    /**
     * Converts accelerometer data to pitch angle in degrees
     */
    fun calculatePitch(x: Float, y: Float, z: Float): Double {
        return atan2(x.toDouble(), sqrt(y * y + z * z).toDouble()) * 180.0 / PI
    }
    
    /**
     * Converts accelerometer data to roll angle in degrees
     */
    fun calculateRoll(x: Float, y: Float, z: Float): Double {
        return atan2(y.toDouble(), sqrt(x * x + z * z).toDouble()) * 180.0 / PI
    }
    
    /**
     * Applies calibration offset to an angle
     */
    fun applyCalibration(angle: Double, offset: Double): Double {
        return angle - offset
    }
    
    /**
     * Normalizes angle to be within -180 to 180 degrees
     */
    fun normalizeAngle(angle: Double): Double {
        var normalized = angle % 360.0
        if (normalized > 180.0) {
            normalized -= 360.0
        } else if (normalized < -180.0) {
            normalized += 360.0
        }
        return normalized
    }
    
    /**
     * Formats angle to specified decimal places
     */
    fun formatAngle(angle: Double, decimalPlaces: Int = 1): String {
        val multiplier = when (decimalPlaces) {
            0 -> 1.0
            1 -> 10.0
            2 -> 100.0
            3 -> 1000.0
            else -> 10.0.pow(decimalPlaces.toDouble())
        }
        val rounded = (angle * multiplier).toLong().toDouble() / multiplier
        return if (decimalPlaces == 0) {
            rounded.toLong().toString()
        } else {
            rounded.toString()
        }
    }
    
    /**
     * Checks if angle is within level threshold (±1 degree)
     */
    fun isLevel(angle: Double, threshold: Double = 1.0): Boolean {
        return abs(angle) <= threshold
    }
    
    /**
     * Calculates the maximum angle from X and Y components
     */
    fun maxAngle(angleX: Double, angleY: Double): Double {
        return maxOf(abs(angleX), abs(angleY))
    }
    
    /**
     * Calculates the magnitude of tilt from both X and Y angles
     */
    fun calculateTiltMagnitude(angleX: Double, angleY: Double): Double {
        return sqrt(angleX * angleX + angleY * angleY)
    }
    
    /**
     * Converts degrees to radians
     */
    fun degreesToRadians(degrees: Double): Double {
        return degrees * PI / 180.0
    }
    
    /**
     * Converts radians to degrees
     */
    fun radiansToDegrees(radians: Double): Double {
        return radians * 180.0 / PI
    }
    
    /**
     * Applies low-pass filter to a single value
     */
    fun lowPassFilter(current: Double, previous: Double, alpha: Double): Double {
        return alpha * previous + (1 - alpha) * current
    }
    
    /**
     * Determines if two angles are approximately equal within tolerance
     */
    fun anglesEqual(angle1: Double, angle2: Double, tolerance: Double = 0.1): Boolean {
        return abs(angle1 - angle2) <= tolerance
    }
}