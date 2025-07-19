package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable

/**
 * Level status enumeration based on angle thresholds
 */
@Serializable
enum class LevelStatus {
    LEVEL,      // Within ±1 degree - Green
    CLOSE,      // 1-5 degrees from level - Yellow/Orange
    NOT_LEVEL;  // >5 degrees from level - Red
    
    /**
     * Gets the color representation for this level status
     */
    val colorHex: String
        get() = when (this) {
            LEVEL -> "#4CAF50"      // Green
            CLOSE -> "#FF9800"      // Orange
            NOT_LEVEL -> "#F44336"  // Red
        }
    
    /**
     * Gets the display name for this level status
     */
    val displayName: String
        get() = when (this) {
            LEVEL -> "Level"
            CLOSE -> "Close"
            NOT_LEVEL -> "Not Level"
        }
    
    /**
     * Gets the description for this level status
     */
    val description: String
        get() = when (this) {
            LEVEL -> "Device is level (±1°)"
            CLOSE -> "Close to level (1-5°)"
            NOT_LEVEL -> "Not level (>5°)"
        }
    
    companion object {
        /**
         * Determines level status from angle measurements
         */
        fun fromAngles(angleX: Double, angleY: Double): LevelStatus {
            val maxAngle = maxOf(kotlin.math.abs(angleX), kotlin.math.abs(angleY))
            return when {
                maxAngle <= 1.0 -> LEVEL
                maxAngle <= 5.0 -> CLOSE
                else -> NOT_LEVEL
            }
        }
        
        /**
         * Determines level status from a single angle
         */
        fun fromAngle(angle: Double): LevelStatus {
            val absAngle = kotlin.math.abs(angle)
            return when {
                absAngle <= 1.0 -> LEVEL
                absAngle <= 5.0 -> CLOSE
                else -> NOT_LEVEL
            }
        }
        
        /**
         * Determines level status from maximum angle
         */
        fun fromMaxAngle(maxAngle: Double): LevelStatus {
            return when {
                maxAngle <= 1.0 -> LEVEL
                maxAngle <= 5.0 -> CLOSE
                else -> NOT_LEVEL
            }
        }
    }
}