package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.LevelMeasurement
import com.keisardev.truelevel.domain.models.MeasurementMode
import com.keisardev.truelevel.domain.models.CalibrationData

/**
 * UI state for the level measurement screen
 */
data class LevelUiState(
    val currentMeasurement: LevelMeasurement? = null,
    val measurementMode: MeasurementMode = MeasurementMode.DIGITAL_INCLINOMETER,
    val isHoldActive: Boolean = false,
    val heldMeasurement: LevelMeasurement? = null,
    val isLogging: Boolean = false,
    val calibrationOffset: CalibrationData? = null,
    val sensorStatus: SensorStatus = SensorStatus.UNKNOWN,
    val batteryOptimizationEnabled: Boolean = false,
    val error: String? = null
)

/**
 * Sensor status enumeration
 */
enum class SensorStatus {
    UNKNOWN,
    AVAILABLE,
    UNAVAILABLE,
    PERMISSION_DENIED,
    ACCURACY_LOW
}