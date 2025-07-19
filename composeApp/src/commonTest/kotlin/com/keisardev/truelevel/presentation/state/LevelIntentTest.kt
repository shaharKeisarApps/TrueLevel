package com.keisardev.truelevel.presentation.state

import com.keisardev.truelevel.domain.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelIntentTest {
    
    @Test
    fun `StartMeasurement intent is object type`() {
        val intent = LevelIntent.StartMeasurement
        assertTrue(intent is LevelIntent.StartMeasurement)
    }
    
    @Test
    fun `StopMeasurement intent is object type`() {
        val intent = LevelIntent.StopMeasurement
        assertTrue(intent is LevelIntent.StopMeasurement)
    }
    
    @Test
    fun `ToggleHold intent is object type`() {
        val intent = LevelIntent.ToggleHold
        assertTrue(intent is LevelIntent.ToggleHold)
    }
    
    @Test
    fun `SwitchMode intent contains mode parameter`() {
        val mode = MeasurementMode.BUBBLE_LEVEL
        val intent = LevelIntent.SwitchMode(mode)
        
        assertTrue(intent is LevelIntent.SwitchMode)
        assertEquals(mode, intent.mode)
    }
    
    @Test
    fun `ApplyCalibration intent contains calibration data`() {
        val calibrationData = CalibrationData.create(
            offsetX = 1.5,
            offsetY = 0.8,
            deviceOrientation = DeviceOrientation.PORTRAIT
        )
        val intent = LevelIntent.ApplyCalibration(calibrationData)
        
        assertTrue(intent is LevelIntent.ApplyCalibration)
        assertEquals(calibrationData, intent.calibrationData)
    }
    
    @Test
    fun `UpdateSensorData intent contains measurement`() {
        val measurement = LevelMeasurement.createNow(
            angleX = 2.0,
            angleY = 1.0,
            accuracy = SensorAccuracy.HIGH
        )
        val intent = LevelIntent.UpdateSensorData(measurement)
        
        assertTrue(intent is LevelIntent.UpdateSensorData)
        assertEquals(measurement, intent.measurement)
    }
    
    @Test
    fun `UpdateSensorStatus intent contains status`() {
        val status = SensorStatus.AVAILABLE
        val intent = LevelIntent.UpdateSensorStatus(status)
        
        assertTrue(intent is LevelIntent.UpdateSensorStatus)
        assertEquals(status, intent.status)
    }
    
    @Test
    fun `HandleError intent contains error message`() {
        val errorMessage = "Test error message"
        val intent = LevelIntent.HandleError(errorMessage)
        
        assertTrue(intent is LevelIntent.HandleError)
        assertEquals(errorMessage, intent.error)
    }
    
    @Test
    fun `all object intents are singleton instances`() {
        val intent1 = LevelIntent.StartMeasurement
        val intent2 = LevelIntent.StartMeasurement
        
        assertTrue(intent1 === intent2)
    }
    
    @Test
    fun `data class intents with same parameters are equal`() {
        val mode = MeasurementMode.ANGLE_DISPLAY
        val intent1 = LevelIntent.SwitchMode(mode)
        val intent2 = LevelIntent.SwitchMode(mode)
        
        assertEquals(intent1, intent2)
    }
    
    @Test
    fun `data class intents with different parameters are not equal`() {
        val intent1 = LevelIntent.SwitchMode(MeasurementMode.DIGITAL_INCLINOMETER)
        val intent2 = LevelIntent.SwitchMode(MeasurementMode.BUBBLE_LEVEL)
        
        assertTrue(intent1 != intent2)
    }
}