package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNull

class LevelUiStateTest {
    
    private val sampleMeasurement = LevelMeasurement.createNow(
        angleX = 1.5,
        angleY = 0.8,
        accuracy = SensorAccuracy.HIGH
    )
    
    private val heldMeasurement = LevelMeasurement.createNow(
        angleX = 2.0,
        angleY = 1.0,
        accuracy = SensorAccuracy.HIGH
    )
    
    @Test
    fun `displayMeasurement returns current measurement when hold is not active`() {
        val state = LevelUiState(
            currentMeasurement = sampleMeasurement,
            isHoldActive = false,
            heldMeasurement = heldMeasurement
        )
        
        assertEquals(sampleMeasurement, state.displayMeasurement)
    }
    
    @Test
    fun `displayMeasurement returns held measurement when hold is active`() {
        val state = LevelUiState(
            currentMeasurement = sampleMeasurement,
            isHoldActive = true,
            heldMeasurement = heldMeasurement
        )
        
        assertEquals(heldMeasurement, state.displayMeasurement)
    }
    
    @Test
    fun `displayMeasurement returns null when no measurements available`() {
        val state = LevelUiState(
            currentMeasurement = null,
            isHoldActive = false,
            heldMeasurement = null
        )
        
        assertNull(state.displayMeasurement)
    }
    
    @Test
    fun `isReady returns true when sensor is available and no error`() {
        val state = LevelUiState(
            sensorStatus = SensorStatus.AVAILABLE,
            error = null
        )
        
        assertTrue(state.isReady)
    }
    
    @Test
    fun `isReady returns false when sensor is not available`() {
        val state = LevelUiState(
            sensorStatus = SensorStatus.UNAVAILABLE,
            error = null
        )
        
        assertFalse(state.isReady)
    }
    
    @Test
    fun `isReady returns false when there is an error`() {
        val state = LevelUiState(
            sensorStatus = SensorStatus.AVAILABLE,
            error = "Test error"
        )
        
        assertFalse(state.isReady)
    }
    
    @Test
    fun `hasActiveOperations returns true when logging is active`() {
        val state = LevelUiState(
            isLogging = true,
            isHoldActive = false
        )
        
        assertTrue(state.hasActiveOperations)
    }
    
    @Test
    fun `hasActiveOperations returns true when hold is active`() {
        val state = LevelUiState(
            isLogging = false,
            isHoldActive = true
        )
        
        assertTrue(state.hasActiveOperations)
    }
    
    @Test
    fun `hasActiveOperations returns true when both logging and hold are active`() {
        val state = LevelUiState(
            isLogging = true,
            isHoldActive = true
        )
        
        assertTrue(state.hasActiveOperations)
    }
    
    @Test
    fun `hasActiveOperations returns false when no operations are active`() {
        val state = LevelUiState(
            isLogging = false,
            isHoldActive = false
        )
        
        assertFalse(state.hasActiveOperations)
    }
    
    @Test
    fun `default state has expected initial values`() {
        val state = LevelUiState()
        
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
}