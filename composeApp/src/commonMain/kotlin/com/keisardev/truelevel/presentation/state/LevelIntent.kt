package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.MeasurementMode
import com.keisardev.truelevel.domain.models.CalibrationData

/**
 * User intents for the level measurement screen
 */
sealed class LevelIntent {
    /**
     * Start sensor measurements
     */
    object StartMeasurement : LevelIntent()
    
    /**
     * Stop sensor measurements
     */
    object StopMeasurement : LevelIntent()
    
    /**
     * Toggle hold/freeze functionality
     */
    object ToggleHold : LevelIntent()
    
    /**
     * Activate hold with current measurement
     */
    object ActivateHold : LevelIntent()
    
    /**
     * Deactivate hold and resume live measurements
     */
    object DeactivateHold : LevelIntent()
    
    /**
     * Toggle measurement logging
     */
    object ToggleLogging : LevelIntent()
    
    /**
     * Switch to a different measurement mode
     */
    data class SwitchMode(val mode: MeasurementMode, val savePreference: Boolean = true) : LevelIntent()
    
    /**
     * Start calibration process
     */
    object StartCalibration : LevelIntent()
    
    /**
     * Apply calibration data
     */
    data class ApplyCalibration(val calibrationData: CalibrationData) : LevelIntent()
    
    /**
     * Reset calibration to default
     */
    object ResetCalibration : LevelIntent()
    
    /**
     * Toggle battery optimization
     */
    object ToggleBatteryOptimization : LevelIntent()
    
    /**
     * Clear current error
     */
    object ClearError : LevelIntent()
    
    /**
     * Handle sensor data update
     */
    data class UpdateSensorData(val measurement: com.keisardev.truelevel.domain.models.LevelMeasurement) : LevelIntent()
    
    /**
     * Handle sensor status change
     */
    data class UpdateSensorStatus(val status: com.keisardev.truelevel.domain.models.SensorStatus) : LevelIntent()
    
    /**
     * Handle error occurrence
     */
    data class HandleError(val error: String) : LevelIntent()
    
    /**
     * Request permission for sensors
     */
    object RequestSensorPermission : LevelIntent()
    
    /**
     * Export measurement data
     */
    object ExportMeasurements : LevelIntent()
    
    /**
     * Clear measurement history
     */
    object ClearMeasurementHistory : LevelIntent()
}