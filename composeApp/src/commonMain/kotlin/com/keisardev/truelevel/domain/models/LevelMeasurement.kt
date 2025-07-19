package com.keisardev.truelevel.domain.models

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * Core measurement data model representing a level measurement
 */
@Serializable
data class LevelMeasurement(
    val angleX: Double,
    val angleY: Double,
    val timestamp: Long,
    val accuracy: SensorAccuracy,
    val isLevel: Boolean,
    val levelStatus: LevelStatus
) {
    companion object {
        /**
         * Creates a LevelMeasurement with calculated level status and provided timestamp
         */
        fun create(
            angleX: Double,
            angleY: Double,
            timestamp: Long,
            accuracy: SensorAccuracy = SensorAccuracy.MEDIUM
        ): LevelMeasurement {
            val levelStatus = LevelStatus.fromAngles(angleX, angleY)
            val isLevel = levelStatus == LevelStatus.LEVEL
            
            return LevelMeasurement(
                angleX = angleX,
                angleY = angleY,
                timestamp = timestamp,
                accuracy = accuracy,
                isLevel = isLevel,
                levelStatus = levelStatus
            )
        }
        
        /**
         * Creates a LevelMeasurement with calculated level status and current timestamp
         */
        fun createNow(
            angleX: Double,
            angleY: Double,
            accuracy: SensorAccuracy = SensorAccuracy.MEDIUM
        ): LevelMeasurement = create(
            angleX = angleX,
            angleY = angleY,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            accuracy = accuracy
        )
    }
    
    /**
     * Gets the maximum angle from both axes
     */
    val maxAngle: Double
        get() = maxOf(kotlin.math.abs(angleX), kotlin.math.abs(angleY))
    
    /**
     * Gets the tilt magnitude (Pythagorean distance)
     */
    val tiltMagnitude: Double
        get() = kotlin.math.sqrt(angleX * angleX + angleY * angleY)
    
    /**
     * Formats the primary angle (X-axis) with 0.1-degree precision
     */
    fun formatPrimaryAngle(): String = AngleFormatter.formatToTenthDegree(angleX)
    
    /**
     * Formats the secondary angle (Y-axis) with 0.1-degree precision
     */
    fun formatSecondaryAngle(): String = AngleFormatter.formatToTenthDegree(angleY)
    
    /**
     * Gets formatted display information for UI
     */
    fun getDisplayInfo(): MeasurementDisplay = AngleFormatter.formatMeasurementDisplay(this)
    
    /**
     * Checks if this measurement is significantly different from another
     */
    fun isDifferentFrom(other: LevelMeasurement, threshold: Double = 0.1): Boolean {
        return kotlin.math.abs(angleX - other.angleX) > threshold ||
               kotlin.math.abs(angleY - other.angleY) > threshold
    }
}