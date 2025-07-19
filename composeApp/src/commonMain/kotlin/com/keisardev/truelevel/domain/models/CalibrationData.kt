package com.keisardev.truelevel.domain.models

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

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
        fun default(): CalibrationData = CalibrationData(
            offsetX = 0.0,
            offsetY = 0.0,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        
        /**
         * Creates a calibration with current timestamp
         */
        fun create(
            offsetX: Double,
            offsetY: Double,
            deviceOrientation: DeviceOrientation = DeviceOrientation.PORTRAIT
        ): CalibrationData = CalibrationData(
            offsetX = offsetX,
            offsetY = offsetY,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            deviceOrientation = deviceOrientation
        )
    }
}