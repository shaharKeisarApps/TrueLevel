package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class LevelStateReducerTest {
    
    private val initialState = LevelStateReducer.createInitialState()
    
    private val sampleMeasurement = LevelMeasurement.createNow(
        angleX = 1.5,
        angleY = 0.8,
        accuracy = SensorAccuracy.HIGH
    )
    
    private val calibrationData = CalibrationData.create(
        offsetX = 0.5,
        offsetY = 0.3,
        deviceOrientation = DeviceOrientation.PORTRAIT
    )
    
    @Test
    fun `createInitialState returns proper default state`() {
        val state = LevelStateReducer.createInitialState()
        
        assertNull(state.currentMeasurement)
        assertEquals(MeasurementMode.DIGITAL_INCLINOMETER, state.measurementMode)
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
        assertFalse(state.isLogging)
        assertNull(state.calibrationOffset)
        assertEquals(SensorStatus.INITIALIZING, state.sensorStatus)
        assertFalse(state.batteryOptimizationEnabled)
        assertFalse(state.isCalibrationRequired)
        assertNull(state.error)
    }
    
    @Test
    fun `StartMeasurement intent initializes sensor status`() {
        val state = initialState.copy(sensorStatus = SensorStatus.UNAVAILABLE)
        val newState = LevelStateReducer.reduce(state, LevelIntent.StartMeasurement)
        
        assertEquals(SensorStatus.INITIALIZING, newState.sensorStatus)
        assertNull(newState.error)
    }
    
    @Test
    fun `StopMeasurement intent clears measurements and hold`() {
        val state = initialState.copy(
            currentMeasurement = sampleMeasurement,
            isHoldActive = true,
            heldMeasurement = sampleMeasurement,
            sensorStatus = SensorStatus.AVAILABLE
        )
        val newState = LevelStateReducer.reduce(state, LevelIntent.StopMeasurement)
        
        assertNull(newState.currentMeasurement)
        assertEquals(SensorStatus.UNAVAILABLE, newState.sensorStatus)
        assertFalse(newState.isHoldActive)
        assertNull(newState.heldMeasurement)
    }
    
    @Test
    fun `ToggleHold activates hold with current measurement`() {
        val state = initialState.copy(
            currentMeasurement = sampleMeasurement,
            isHoldActive = false
        )
        val newState = LevelStateReducer.reduce(state, LevelIntent.ToggleHold)
        
        assertTrue(newState.isHoldActive)
        assertEquals(sampleMeasurement, newState.heldMeasurement)
    }
    
    @Test
    fun `ToggleHold deactivates hold and clears held measurement`() {
        val state = initialState.copy(
            currentMeasurement = sampleMeasurement,
            isHoldActive = true,
            heldMeasurement = sampleMeasurement
        )
        val newState = LevelStateReducer.reduce(state, LevelIntent.ToggleHold)
        
        assertFalse(newState.isHoldActive)
        assertNull(newState.heldMeasurement)
    }
    
    @Test
    fun `ActivateHold sets hold with current measurement`() {
        val state = initialState.copy(currentMeasurement = sampleMeasurement)
        val newState = LevelStateReducer.reduce(state, LevelIntent.ActivateHold)
        
        assertTrue(newState.isHoldActive)
        assertEquals(sampleMeasurement, newState.heldMeasurement)
    }
    
    @Test
    fun `DeactivateHold clears hold state`() {
        val state = initialState.copy(
            isHoldActive = true,
            heldMeasurement = sampleMeasurement
        )
        val newState = LevelStateReducer.reduce(state, LevelIntent.DeactivateHold)
        
        assertFalse(newState.isHoldActive)
        assertNull(newState.heldMeasurement)
    }
    
    @Test
    fun `ToggleLogging toggles logging state`() {
        val state = initialState.copy(isLogging = false)
        val newState = LevelStateReducer.reduce(state, LevelIntent.ToggleLogging)
        
        assertTrue(newState.isLogging)
        
        val toggledBack = LevelStateReducer.reduce(newState, LevelIntent.ToggleLogging)
        assertFalse(toggledBack.isLogging)
    }
    
    @Test
    fun `SwitchMode changes mode and clears hold`() {
        val state = initialState.copy(
            measurementMode = MeasurementMode.DIGITAL_INCLINOMETER,
            isHoldActive = true,
            heldMeasurement = sampleMeasurement
        )
        val newState = LevelStateReducer.reduce(
            state, 
            LevelIntent.SwitchMode(MeasurementMode.BUBBLE_LEVEL)
        )
        
        assertEquals(MeasurementMode.BUBBLE_LEVEL, newState.measurementMode)
        assertFalse(newState.isHoldActive)
        assertNull(newState.heldMeasurement)
    }
    
    @Test
    fun `ApplyCalibration sets calibration data`() {
        val newState = LevelStateReducer.reduce(
            initialState, 
            LevelIntent.ApplyCalibration(calibrationData)
        )
        
        assertEquals(calibrationData, newState.calibrationOffset)
        assertFalse(newState.isCalibrationRequired)
    }
    
    @Test
    fun `ResetCalibration sets default calibration`() {
        val state = initialState.copy(
            calibrationOffset = calibrationData,
            isCalibrationRequired = true
        )
        val newState = LevelStateReducer.reduce(state, LevelIntent.ResetCalibration)
        
        assertNotNull(newState.calibrationOffset)
        assertEquals(0.0, newState.calibrationOffset!!.offsetX)
        assertEquals(0.0, newState.calibrationOffset!!.offsetY)
        assertFalse(newState.isCalibrationRequired)
    }
    
    @Test
    fun `UpdateSensorData updates measurement and status`() {
        val state = initialState.copy(sensorStatus = SensorStatus.INITIALIZING)
        val newState = LevelStateReducer.reduce(
            state, 
            LevelIntent.UpdateSensorData(sampleMeasurement)
        )
        
        assertEquals(sampleMeasurement, newState.currentMeasurement)
        assertEquals(SensorStatus.AVAILABLE, newState.sensorStatus)
    }
    
    @Test
    fun `UpdateSensorStatus updates sensor status`() {
        val newState = LevelStateReducer.reduce(
            initialState, 
            LevelIntent.UpdateSensorStatus(SensorStatus.PERMISSION_DENIED)
        )
        
        assertEquals(SensorStatus.PERMISSION_DENIED, newState.sensorStatus)
    }
    
    @Test
    fun `HandleError sets error and error status`() {
        val errorMessage = "Test error"
        val newState = LevelStateReducer.reduce(
            initialState, 
            LevelIntent.HandleError(errorMessage)
        )
        
        assertEquals(errorMessage, newState.error)
        assertEquals(SensorStatus.ERROR, newState.sensorStatus)
    }
    
    @Test
    fun `ClearError removes error`() {
        val state = initialState.copy(error = "Test error")
        val newState = LevelStateReducer.reduce(state, LevelIntent.ClearError)
        
        assertNull(newState.error)
    }
    
    @Test
    fun `updateMeasurement doesn't update when hold is active`() {
        val state = initialState.copy(
            currentMeasurement = sampleMeasurement,
            isHoldActive = true
        )
        val newMeasurement = LevelMeasurement.createNow(
            angleX = 5.0,
            angleY = 3.0,
            accuracy = SensorAccuracy.HIGH
        )
        
        val newState = LevelStateReducer.updateMeasurement(state, newMeasurement)
        
        assertEquals(sampleMeasurement, newState.currentMeasurement)
    }
    
    @Test
    fun `updateMeasurement updates when measurement is significantly different`() {
        val state = initialState.copy(currentMeasurement = sampleMeasurement)
        val newMeasurement = LevelMeasurement.createNow(
            angleX = 5.0,
            angleY = 3.0,
            accuracy = SensorAccuracy.HIGH
        )
        
        val newState = LevelStateReducer.updateMeasurement(state, newMeasurement)
        
        assertEquals(newMeasurement, newState.currentMeasurement)
        assertEquals(SensorStatus.AVAILABLE, newState.sensorStatus)
    }
    
    @Test
    fun `switchMode clears hold state and mode-specific errors`() {
        val state = initialState.copy(
            measurementMode = MeasurementMode.DIGITAL_INCLINOMETER,
            isHoldActive = true,
            heldMeasurement = sampleMeasurement,
            error = "Mode specific error"
        )
        
        val newState = LevelStateReducer.switchMode(state, MeasurementMode.BUBBLE_LEVEL)
        
        assertEquals(MeasurementMode.BUBBLE_LEVEL, newState.measurementMode)
        assertFalse(newState.isHoldActive)
        assertNull(newState.heldMeasurement)
        assertNull(newState.error)
    }
    
    @Test
    fun `validateState ensures consistency`() {
        val inconsistentState = initialState.copy(
            isHoldActive = false,
            heldMeasurement = sampleMeasurement, // Should be null when hold is not active
            sensorStatus = SensorStatus.ERROR,
            calibrationOffset = null,
            isCalibrationRequired = false // Should be true when error and no calibration
        )
        
        val validatedState = LevelStateReducer.validateState(inconsistentState)
        
        assertNull(validatedState.heldMeasurement)
        assertTrue(validatedState.isCalibrationRequired)
    }
}