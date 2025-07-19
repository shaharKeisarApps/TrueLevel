package com.keisardev.truelevel.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SensorDataTest {
    
    @Test
    fun testSensorDataCreation() {
        val sensorData = SensorData(
            accelerometerX = 1.0f,
            accelerometerY = 2.0f,
            accelerometerZ = 9.8f,
            gyroscopeX = 0.1f,
            gyroscopeY = 0.2f,
            gyroscopeZ = 0.3f,
            timestamp = 1234567890L,
            accuracy = SensorAccuracy.HIGH
        )
        
        assertEquals(1.0f, sensorData.accelerometerX)
        assertEquals(2.0f, sensorData.accelerometerY)
        assertEquals(9.8f, sensorData.accelerometerZ)
        assertEquals(0.1f, sensorData.gyroscopeX)
        assertEquals(0.2f, sensorData.gyroscopeY)
        assertEquals(0.3f, sensorData.gyroscopeZ)
        assertEquals(1234567890L, sensorData.timestamp)
        assertEquals(SensorAccuracy.HIGH, sensorData.accuracy)
    }
    
    @Test
    fun testHasGyroscopeData() {
        val withGyroscope = SensorData(
            accelerometerX = 1.0f,
            accelerometerY = 2.0f,
            accelerometerZ = 9.8f,
            gyroscopeX = 0.1f,
            gyroscopeY = 0.2f,
            gyroscopeZ = 0.3f,
            timestamp = 1234567890L
        )
        
        val withoutGyroscope = SensorData(
            accelerometerX = 1.0f,
            accelerometerY = 2.0f,
            accelerometerZ = 9.8f,
            timestamp = 1234567890L
        )
        
        assertTrue(withGyroscope.hasGyroscopeData)
        assertFalse(withoutGyroscope.hasGyroscopeData)
    }
    
    @Test
    fun testCreateNow() {
        val sensorData = SensorData.createNow(
            accelerometerX = 1.0f,
            accelerometerY = 2.0f,
            accelerometerZ = 9.8f,
            gyroscopeX = 0.1f,
            gyroscopeY = 0.2f,
            gyroscopeZ = 0.3f,
            accuracy = SensorAccuracy.HIGH
        )
        
        assertEquals(1.0f, sensorData.accelerometerX)
        assertEquals(2.0f, sensorData.accelerometerY)
        assertEquals(9.8f, sensorData.accelerometerZ)
        assertEquals(0.1f, sensorData.gyroscopeX)
        assertEquals(0.2f, sensorData.gyroscopeY)
        assertEquals(0.3f, sensorData.gyroscopeZ)
        assertEquals(SensorAccuracy.HIGH, sensorData.accuracy)
        assertTrue(sensorData.timestamp > 0)
    }
    
    @Test
    fun testDefaultAccuracy() {
        val sensorData = SensorData(
            accelerometerX = 1.0f,
            accelerometerY = 2.0f,
            accelerometerZ = 9.8f,
            timestamp = 1234567890L
        )
        
        assertEquals(SensorAccuracy.MEDIUM, sensorData.accuracy)
    }
}