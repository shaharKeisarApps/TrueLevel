package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime


/**
 * Calibration data for sensor offset correction
 */
@Serializable
data class CalibrationData(
    val offsetX: Double,
    val offsetY: Double,
    val timestamp: Long,
    val deviceOrientation: DeviceOrientation = DeviceOrientation.PORTRAIT
) {
    companion object {
        /**
         * Creates a default calibration with zero offsets
         */
        @OptIn(ExperimentalTime::class)
        fun default(): CalibrationData = CalibrationData(
            offsetX = 0.0,
            offsetY = 0.0,
            timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
        )
        
        /**
         * Creates a calibration with current timestamp
         */
        @OptIn(ExperimentalTime::class)
        fun create(
            offsetX: Double,
            offsetY: Double,
            deviceOrientation: DeviceOrientation = DeviceOrientation.PORTRAIT
        ): CalibrationData = CalibrationData(
            offsetX = offsetX,
            offsetY = offsetY,
            timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds(),
            deviceOrientation = deviceOrientation
        )
    }
}