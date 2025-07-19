package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.LevelMeasurement
import com.keisardev.truelevel.domain.models.MeasurementMode
import com.keisardev.truelevel.domain.models.CalibrationData
import com.keisardev.truelevel.domain.models.SensorStatus

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
    val sensorStatus: SensorStatus = SensorStatus.INITIALIZING,
    val batteryOptimizationEnabled: Boolean = false,
    val isCalibrationRequired: Boolean = false,
    val error: String? = null
) {
    /**
     * Gets the measurement to display (held measurement if active, otherwise current)
     */
    val displayMeasurement: LevelMeasurement?
        get() = if (isHoldActive) heldMeasurement else currentMeasurement
    
    /**
     * Checks if the app is in a ready state for measurements
     */
    val isReady: Boolean
        get() = sensorStatus == SensorStatus.AVAILABLE && error == null
    
    /**
     * Checks if there are any active operations
     */
    val hasActiveOperations: Boolean
        get() = isLogging || isHoldActive
}