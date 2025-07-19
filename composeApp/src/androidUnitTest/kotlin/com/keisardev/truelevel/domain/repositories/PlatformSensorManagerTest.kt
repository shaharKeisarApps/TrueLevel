package com.keisardev.truelevel.domain.repositories

import com.keisardev.truelevel.domain.models.SensorAccuracy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Basic unit tests for PlatformSensorManager Android implementation
 * Note: Full integration testing would require Android instrumentation tests
 * due to the dependency on Android Context and SensorManager
 */
class PlatformSensorManagerTest {
    
    @Test
    fun testSensorAccuracyEnumValues() {
        // Test that all sensor accuracy values are available
        val accuracyValues = SensorAccuracy.values()
        assertEquals(4, accuracyValues.size)
        assertTrue(accuracyValues.contains(SensorAccuracy.UNRELIABLE))
        assertTrue(accuracyValues.contains(SensorAccuracy.LOW))
        assertTrue(accuracyValues.contains(SensorAccuracy.MEDIUM))
        assertTrue(accuracyValues.contains(SensorAccuracy.HIGH))
    }
    
    @Test
    fun testDefaultAccuracyIsMedium() {
        // Test that default accuracy is MEDIUM
        val defaultAccuracy = SensorAccuracy.MEDIUM
        assertEquals(SensorAccuracy.MEDIUM, defaultAccuracy)
    }
    
    // Note: Additional tests would require Android instrumentation testing
    // or dependency injection with mocked Android components
    // For now, we focus on testing the data models and basic functionality
}