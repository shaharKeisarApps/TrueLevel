package com.keisardev.truelevel.domain.models

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
}