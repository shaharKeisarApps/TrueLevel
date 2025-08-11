package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

/**
 * Raw sensor data from accelerometer and gyroscope
 */
@Serializable
data class SensorData(
    val accelerometerX: Float,
    val accelerometerY: Float,
    val accelerometerZ: Float,
    val gyroscopeX: Float? = null,
    val gyroscopeY: Float? = null,
    val gyroscopeZ: Float? = null,
    val timestamp: Long,
    val accuracy: SensorAccuracy = SensorAccuracy.MEDIUM
) {
    /**
     * Checks if gyroscope data is available
     */
    val hasGyroscopeData: Boolean
        get() = gyroscopeX != null && gyroscopeY != null && gyroscopeZ != null
    
    companion object {
        /**
         * Creates SensorData with current timestamp
         */
        @OptIn(ExperimentalTime::class)
        fun createNow(
            accelerometerX: Float,
            accelerometerY: Float,
            accelerometerZ: Float,
            gyroscopeX: Float? = null,
            gyroscopeY: Float? = null,
            gyroscopeZ: Float? = null,
            accuracy: SensorAccuracy = SensorAccuracy.MEDIUM
        ): SensorData = SensorData(
            accelerometerX = accelerometerX,
            accelerometerY = accelerometerY,
            accelerometerZ = accelerometerZ,
            gyroscopeX = gyroscopeX,
            gyroscopeY = gyroscopeY,
            gyroscopeZ = gyroscopeZ,
            timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds(),
            accuracy = accuracy
        )
    }
}