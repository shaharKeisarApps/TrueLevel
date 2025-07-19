package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.LevelMeasurement
import com.keisardev.truelevel.domain.models.MeasurementMode
import com.keisardev.truelevel.domain.models.CalibrationData
import com.keisardev.truelevel.domain.models.SensorStatus

/**
 * State transformation functions for reactive UI updates
 */
object LevelStateReducer {
    
    /**
     * Reduces the current state based on the given intent
     */
    fun reduce(currentState: LevelUiState, intent: LevelIntent): LevelUiState {
        return when (intent) {
            is LevelIntent.StartMeasurement -> currentState.copy(
                sensorStatus = SensorStatus.INITIALIZING,
                error = null
            )
            
            is LevelIntent.StopMeasurement -> currentState.copy(
                currentMeasurement = null,
                sensorStatus = SensorStatus.UNAVAILABLE,
                isHoldActive = false,
                heldMeasurement = null
            )
            
            is LevelIntent.ToggleHold -> currentState.copy(
                isHoldActive = !currentState.isHoldActive,
                heldMeasurement = if (!currentState.isHoldActive) {
                    currentState.currentMeasurement
                } else {
                    null
                }
            )
            
            is LevelIntent.ActivateHold -> currentState.copy(
                isHoldActive = true,
                heldMeasurement = currentState.currentMeasurement
            )
            
            is LevelIntent.DeactivateHold -> currentState.copy(
                isHoldActive = false,
                heldMeasurement = null
            )
            
            is LevelIntent.ToggleLogging -> currentState.copy(
                isLogging = !currentState.isLogging
            )
            
            is LevelIntent.SwitchMode -> currentState.copy(
                measurementMode = intent.mode,
                // Clear hold when switching modes for consistency
                isHoldActive = false,
                heldMeasurement = null
            )
            
            is LevelIntent.StartCalibration -> currentState.copy(
                isCalibrationRequired = false,
                error = null
            )
            
            is LevelIntent.ApplyCalibration -> currentState.copy(
                calibrationOffset = intent.calibrationData,
                isCalibrationRequired = false
            )
            
            is LevelIntent.ResetCalibration -> currentState.copy(
                calibrationOffset = CalibrationData.default(),
                isCalibrationRequired = false
            )
            
            is LevelIntent.ToggleBatteryOptimization -> currentState.copy(
                batteryOptimizationEnabled = !currentState.batteryOptimizationEnabled
            )
            
            is LevelIntent.ClearError -> currentState.copy(
                error = null
            )
            
            is LevelIntent.UpdateSensorData -> currentState.copy(
                currentMeasurement = intent.measurement,
                sensorStatus = if (currentState.sensorStatus == SensorStatus.INITIALIZING) {
                    SensorStatus.AVAILABLE
                } else {
                    currentState.sensorStatus
                }
            )
            
            is LevelIntent.UpdateSensorStatus -> currentState.copy(
                sensorStatus = intent.status,
                isCalibrationRequired = intent.status == SensorStatus.ERROR && 
                    currentState.calibrationOffset == null
            )
            
            is LevelIntent.HandleError -> currentState.copy(
                error = intent.error,
                sensorStatus = SensorStatus.ERROR
            )
            
            is LevelIntent.RequestSensorPermission -> currentState.copy(
                sensorStatus = SensorStatus.INITIALIZING,
                error = null
            )
            
            // These intents don't directly modify UI state but trigger side effects
            is LevelIntent.ExportMeasurements,
            is LevelIntent.ClearMeasurementHistory -> currentState
        }
    }
    
    /**
     * Creates an initial state for the application
     */
    fun createInitialState(): LevelUiState = LevelUiState()
    
    /**
     * Validates state transitions and ensures consistency
     */
    fun validateState(state: LevelUiState): LevelUiState {
        return state.copy(
            // Ensure held measurement is cleared if hold is not active
            heldMeasurement = if (state.isHoldActive) state.heldMeasurement else null,
            
            // Ensure calibration requirement is set appropriately
            isCalibrationRequired = state.isCalibrationRequired || 
                (state.sensorStatus == SensorStatus.ERROR && state.calibrationOffset == null)
        )
    }
    
    /**
     * Handles measurement updates with proper filtering and validation
     */
    fun updateMeasurement(
        currentState: LevelUiState, 
        newMeasurement: LevelMeasurement
    ): LevelUiState {
        // Don't update if hold is active
        if (currentState.isHoldActive) {
            return currentState
        }
        
        // Apply basic filtering - only update if measurement is significantly different
        val shouldUpdate = currentState.currentMeasurement?.let { current ->
            newMeasurement.isDifferentFrom(current, threshold = 0.05)
        } ?: true
        
        return if (shouldUpdate) {
            currentState.copy(
                currentMeasurement = newMeasurement,
                sensorStatus = SensorStatus.AVAILABLE
            )
        } else {
            currentState
        }
    }
    
    /**
     * Handles mode switching with proper state cleanup
     */
    fun switchMode(currentState: LevelUiState, newMode: MeasurementMode): LevelUiState {
        return currentState.copy(
            measurementMode = newMode,
            // Clear hold state when switching modes for better UX
            isHoldActive = false,
            heldMeasurement = null,
            // Clear any mode-specific errors
            error = if (currentState.error?.contains("mode", ignoreCase = true) == true) {
                null
            } else {
                currentState.error
            }
        )
    }
}