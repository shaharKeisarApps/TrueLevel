package com.keisardev.truelevel.domain.models

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class LevelMeasurementTest {
    
    @Test
    fun testLevelMeasurementCreation() {
        val measurement = LevelMeasurement.create(
            angleX = 0.5,
            angleY = 0.3,
            timestamp = 1234567890L
        )
        
        assertEquals(0.5, measurement.angleX)
        assertEquals(0.3, measurement.angleY)
        assertEquals(1234567890L, measurement.timestamp)
        assertEquals(LevelStatus.LEVEL, measurement.levelStatus)
        assertTrue(measurement.isLevel)
    }
    
    @Test
    fun testLevelStatusCalculation() {
        // Test LEVEL status (within ±1 degree)
        val levelMeasurement = LevelMeasurement.createNow(angleX = 0.8, angleY = 0.5)
        assertEquals(LevelStatus.LEVEL, levelMeasurement.levelStatus)
        
        // Test CLOSE status (1-5 degrees)
        val closeMeasurement = LevelMeasurement.createNow(angleX = 3.0, angleY = 2.0)
        assertEquals(LevelStatus.CLOSE, closeMeasurement.levelStatus)
        
        // Test NOT_LEVEL status (>5 degrees)
        val notLevelMeasurement = LevelMeasurement.createNow(angleX = 10.0, angleY = 2.0)
        assertEquals(LevelStatus.NOT_LEVEL, notLevelMeasurement.levelStatus)
    }
    
    @Test
    fun testSerialization() {
        val measurement = LevelMeasurement.create(
            angleX = 1.5,
            angleY = 2.3,
            timestamp = 1234567890L
        )
        
        val json = Json.encodeToString(LevelMeasurement.serializer(), measurement)
        val deserialized = Json.decodeFromString(LevelMeasurement.serializer(), json)
        
        assertEquals(measurement, deserialized)
    }
    
    @Test
    fun testMaxAngleCalculation() {
        val measurement1 = LevelMeasurement.create(angleX = 2.0, angleY = 3.0, timestamp = 0L)
        assertEquals(3.0, measurement1.maxAngle)
        
        val measurement2 = LevelMeasurement.create(angleX = -5.0, angleY = 2.0, timestamp = 0L)
        assertEquals(5.0, measurement2.maxAngle)
        
        val measurement3 = LevelMeasurement.create(angleX = 0.0, angleY = 0.0, timestamp = 0L)
        assertEquals(0.0, measurement3.maxAngle)
    }
    
    @Test
    fun testTiltMagnitudeCalculation() {
        val measurement1 = LevelMeasurement.create(angleX = 3.0, angleY = 4.0, timestamp = 0L)
        assertEquals(5.0, measurement1.tiltMagnitude, 0.01) // 3-4-5 triangle
        
        val measurement2 = LevelMeasurement.create(angleX = 0.0, angleY = 0.0, timestamp = 0L)
        assertEquals(0.0, measurement2.tiltMagnitude, 0.01)
        
        val measurement3 = LevelMeasurement.create(angleX = 1.0, angleY = 1.0, timestamp = 0L)
        assertEquals(1.414, measurement3.tiltMagnitude, 0.01) // sqrt(2)
    }
    
    @Test
    fun testAngleFormatting() {
        val measurement = LevelMeasurement.create(angleX = 1.2, angleY = -2.5, timestamp = 0L)
        
        assertEquals("1.2", measurement.formatPrimaryAngle())
        assertEquals("-2.5", measurement.formatSecondaryAngle())
    }
    
    @Test
    fun testDisplayInfo() {
        val measurement = LevelMeasurement.create(
            angleX = 1.5,
            angleY = -2.3,
            timestamp = 0L,
            accuracy = SensorAccuracy.HIGH
        )
        
        val displayInfo = measurement.getDisplayInfo()
        
        assertEquals("1.5°", displayInfo.primaryAngle)
        assertEquals("-2.3°", displayInfo.secondaryAngle)
        assertEquals("Close", displayInfo.statusText)
        assertEquals("#FF9800", displayInfo.statusColor)
        assertEquals("X: 1.5°, Y: -2.3°", displayInfo.dualAxisText)
        assertTrue(displayInfo.accessibilityText.contains("Close to level"))
    }
    
    @Test
    fun testIsDifferentFrom() {
        val measurement1 = LevelMeasurement.create(angleX = 1.0, angleY = 2.0, timestamp = 0L)
        val measurement2 = LevelMeasurement.create(angleX = 1.05, angleY = 2.05, timestamp = 0L)
        val measurement3 = LevelMeasurement.create(angleX = 1.2, angleY = 2.0, timestamp = 0L)
        
        // Should not be different (within default threshold of 0.1)
        assertFalse(measurement1.isDifferentFrom(measurement2))
        
        // Should be different (exceeds threshold)
        assertTrue(measurement1.isDifferentFrom(measurement3))
        
        // Test with custom threshold
        assertTrue(measurement1.isDifferentFrom(measurement2, 0.01))
        assertFalse(measurement1.isDifferentFrom(measurement3, 0.5))
    }
    
    @Test
    fun testLevelStatusUsesNewLogic() {
        // Verify that LevelMeasurement uses the new LevelStatus.fromAngles logic
        val measurement1 = LevelMeasurement.create(angleX = 0.5, angleY = 6.0, timestamp = 0L)
        assertEquals(LevelStatus.NOT_LEVEL, measurement1.levelStatus) // Max angle is 6.0
        assertFalse(measurement1.isLevel)
        
        val measurement2 = LevelMeasurement.create(angleX = 2.0, angleY = 0.5, timestamp = 0L)
        assertEquals(LevelStatus.CLOSE, measurement2.levelStatus) // Max angle is 2.0
        assertFalse(measurement2.isLevel)
        
        val measurement3 = LevelMeasurement.create(angleX = 0.8, angleY = 0.9, timestamp = 0L)
        assertEquals(LevelStatus.LEVEL, measurement3.levelStatus) // Max angle is 0.9
        assertTrue(measurement3.isLevel)
    }
}