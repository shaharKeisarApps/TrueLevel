package com.keisardev.truelevel.domain.repositories

import com.keisardev.truelevel.domain.models.CalibrationData
import com.keisardev.truelevel.domain.models.SensorAccuracy
import com.keisardev.truelevel.domain.models.SensorData
import com.keisardev.truelevel.domain.models.SensorStatus
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform sensor interface for accessing device sensors
 */
interface SensorManager {
    /**
     * Start sensor updates and return a flow of sensor data
     */
    fun startSensorUpdates(): Flow<SensorData>
    
    /**
     * Stop sensor updates
     */
    suspend fun stopSensorUpdates()
    
    /**
     * Check if sensors are available on the device
     */
    suspend fun isAvailable(): Boolean
    
    /**
     * Get current sensor accuracy
     */
    suspend fun getCurrentAccuracy(): SensorAccuracy
    
    /**
     * Get current sensor status
     */
    suspend fun getSensorStatus(): SensorStatus
    
    /**
     * Perform sensor calibration
     */
    suspend fun calibrate(): CalibrationData
    
    /**
     * Check if gyroscope is available
     */
    suspend fun isGyroscopeAvailable(): Boolean
    
    /**
     * Check if accelerometer is available
     */
    suspend fun isAccelerometerAvailable(): Boolean
    
    /**
     * Request sensor permissions (platform-specific)
     */
    suspend fun requestPermissions(): Boolean
}

