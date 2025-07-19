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
            val maxAngle = maxOf(kotlin.math.abs(angleX), kotlin.math.abs(angleY))
            val levelStatus = when {
                maxAngle <= 1.0 -> LevelStatus.LEVEL
                maxAngle <= 5.0 -> LevelStatus.CLOSE
                else -> LevelStatus.NOT_LEVEL
            }
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
}