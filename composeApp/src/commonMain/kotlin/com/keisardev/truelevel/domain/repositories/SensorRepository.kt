package com.keisardev.truelevel.domain.repositories

import com.keisardev.truelevel.domain.models.SensorData
import com.keisardev.truelevel.domain.models.SensorAccuracy
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for sensor data access
 */
interface SensorRepository {
    /**
     * Start collecting sensor data
     */
    fun startSensorUpdates(): Flow<SensorData>
    
    /**
     * Stop collecting sensor data
     */
    suspend fun stopSensorUpdates()
    
    /**
     * Check if sensors are available
     */
    suspend fun isSensorAvailable(): Boolean
    
    /**
     * Get current sensor accuracy
     */
    suspend fun getCurrentAccuracy(): SensorAccuracy
}