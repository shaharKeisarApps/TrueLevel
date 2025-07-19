package com.keisardev.truelevel.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class MeasurementProcessorTest {
    
    private val processor = MeasurementProcessor()
    
    @Test
    fun testProcessSensorDataWithoutCalibration() {
        // Test level sensor data (device flat)
        val levelSensorData = SensorData.createNow(
            accelerometerX = 0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f,
            accuracy = SensorAccuracy.HIGH
        )
        
        val measurement = processor.processSensorData(levelSensorData)
        
        assertEquals(0.0, measurement.angleX, 0.1)
        assertEquals(0.0, measurement.angleY, 0.1)
        assertEquals(LevelStatus.LEVEL, measurement.levelStatus)
        assertTrue(measurement.isLevel)
        assertEquals(SensorAccuracy.HIGH, measurement.accuracy)
    }
    
    @Test
    fun testProcessSensorDataWithCalibration() {
        // Test sensor data with 2-degree offset
        val sensorData = SensorData.createNow(
            accelerometerX = 0.34f, // ~2 degrees
            accelerometerY = 0.17f, // ~1 degree
            accelerometerZ = 9.8f
        )
        
        val calibration = CalibrationData.create(
            offsetX = 2.0,
            offsetY = 1.0
        )
        
        val measurement = processor.processSensorData(sensorData, calibration)
        
        // After calibration, should be close to level
        assertEquals(0.0, measurement.angleX, 0.2)
        assertEquals(0.0, measurement.angleY, 0.2)
        assertEquals(LevelStatus.LEVEL, measurement.levelStatus)
    }
    
    @Test
    fun testLowPassFilteringReducesNoise() {
        processor.resetFilter()
        
        // First reading - no filtering yet
        val firstReading = SensorData.createNow(
            accelerometerX = 1.0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val firstMeasurement = processor.processSensorData(firstReading)
        
        // Second reading with noise - should be filtered
        val noisyReading = SensorData.createNow(
            accelerometerX = 5.0f, // High noise
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val filteredMeasurement = processor.processSensorData(noisyReading)
        
        // Filtered result should be between first and noisy reading
        assertTrue(filteredMeasurement.angleX > firstMeasurement.angleX)
        assertTrue(filteredMeasurement.angleX < 25.0) // Less than unfiltered noisy reading
    }
    
    @Test
    fun testAngleNormalization() {
        // Test angle normalization for extreme values
        val extremeSensorData = SensorData.createNow(
            accelerometerX = -9.8f, // Should give ~-90 degrees
            accelerometerY = 0f,
            accelerometerZ = 0.1f
        )
        
        val measurement = processor.processSensorData(extremeSensorData)
        
        // Angle should be normalized to -180 to 180 range
        assertTrue(measurement.angleX >= -180.0)
        assertTrue(measurement.angleX <= 180.0)
        assertTrue(measurement.angleY >= -180.0)
        assertTrue(measurement.angleY <= 180.0)
    }
    
    @Test
    fun testLevelStatusDetermination() {
        // Create a fresh processor for each test to avoid filter state issues
        val testProcessor = MeasurementProcessor()
        
        // Test LEVEL status (within ±1 degree)
        // For 1 degree: tan(1°) * 9.8 ≈ 0.171
        val levelData = SensorData.createNow(
            accelerometerX = 0.171f, // ~1 degree
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val levelMeasurement = testProcessor.processSensorData(levelData)
        assertEquals(LevelStatus.LEVEL, levelMeasurement.levelStatus)
        assertTrue(levelMeasurement.isLevel)
        
        // Test CLOSE status (1-5 degrees) - use fresh processor
        val testProcessor2 = MeasurementProcessor()
        // For 3 degrees: tan(3°) * 9.8 ≈ 0.513
        val closeData = SensorData.createNow(
            accelerometerX = 0.513f, // ~3 degrees
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val closeMeasurement = testProcessor2.processSensorData(closeData)
        assertEquals(LevelStatus.CLOSE, closeMeasurement.levelStatus)
        assertFalse(closeMeasurement.isLevel)
        
        // Test NOT_LEVEL status (>5 degrees) - use fresh processor
        val testProcessor3 = MeasurementProcessor()
        // For 10 degrees: tan(10°) * 9.8 ≈ 1.728
        val notLevelData = SensorData.createNow(
            accelerometerX = 1.728f, // ~10 degrees
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val notLevelMeasurement = testProcessor3.processSensorData(notLevelData)
        assertEquals(LevelStatus.NOT_LEVEL, notLevelMeasurement.levelStatus)
        assertFalse(notLevelMeasurement.isLevel)
    }
    
    @Test
    fun testFilterReset() {
        // Process some data to initialize filter
        val initialData = SensorData.createNow(
            accelerometerX = 1.0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        processor.processSensorData(initialData)
        
        // Reset filter
        processor.resetFilter()
        
        // Next reading should not be affected by previous readings
        val newData = SensorData.createNow(
            accelerometerX = 0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val measurement = processor.processSensorData(newData)
        
        assertEquals(0.0, measurement.angleX, 0.1)
        assertEquals(0.0, measurement.angleY, 0.1)
    }
    
    @Test
    fun testFilterStrengthSettings() {
        // Test different filter strengths
        processor.setFilterStrength(FilterStrength.NONE)
        processor.setFilterStrength(FilterStrength.LOW)
        processor.setFilterStrength(FilterStrength.MEDIUM)
        processor.setFilterStrength(FilterStrength.HIGH)
        
        // Should not throw exceptions and should reset filter when changing
        val testData = SensorData.createNow(
            accelerometerX = 0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f
        )
        val measurement = processor.processSensorData(testData)
        assertNotNull(measurement)
    }
    
    @Test
    fun testCreateCalibrationFromLevel() {
        // Test creating calibration from sensor data
        val sensorData = SensorData.createNow(
            accelerometerX = 0.34f, // ~2 degrees offset
            accelerometerY = 0.17f, // ~1 degree offset
            accelerometerZ = 9.8f
        )
        
        val calibration = MeasurementProcessor.createCalibrationFromLevel(sensorData)
        
        assertEquals(2.0, calibration.offsetX, 0.2)
        assertEquals(1.0, calibration.offsetY, 0.2)
        assertTrue(calibration.timestamp > 0)
    }
    
    @Test
    fun testTimestampPreservation() {
        val originalTimestamp = 1234567890L
        val sensorData = SensorData(
            accelerometerX = 0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f,
            timestamp = originalTimestamp,
            accuracy = SensorAccuracy.MEDIUM
        )
        
        val measurement = processor.processSensorData(sensorData)
        assertEquals(originalTimestamp, measurement.timestamp)
    }
    
    @Test
    fun testAccuracyPreservation() {
        val sensorData = SensorData.createNow(
            accelerometerX = 0f,
            accelerometerY = 0f,
            accelerometerZ = 9.8f,
            accuracy = SensorAccuracy.LOW
        )
        
        val measurement = processor.processSensorData(sensorData)
        assertEquals(SensorAccuracy.LOW, measurement.accuracy)
    }
    
    @Test
    fun testBothAxesContributeToLevelStatus() {
        // Reset processor to ensure clean state
        processor.resetFilter()
        
        // Test that both X and Y angles contribute to level status
        // For 3 degrees: tan(3°) * 9.8 ≈ 0.513
        val sensorData = SensorData.createNow(
            accelerometerX = 0.171f, // ~1 degree (LEVEL individually)
            accelerometerY = 0.513f, // ~3 degrees (CLOSE individually)
            accelerometerZ = 9.8f
        )
        
        val measurement = processor.processSensorData(sensorData)
        
        // Should be CLOSE because max angle is ~3 degrees
        assertEquals(LevelStatus.CLOSE, measurement.levelStatus)
        assertFalse(measurement.isLevel)
    }
}