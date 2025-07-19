package com.keisardev.truelevel.domain.models

/**
 * Callback interface for sensor data updates
 */
interface SensorCallback {
    /**
     * Called when new sensor data is available
     */
    fun onSensorDataReceived(sensorData: SensorData)
    
    /**
     * Called when sensor accuracy changes
     */
    fun onAccuracyChanged(accuracy: SensorAccuracy)
    
    /**
     * Called when sensor status changes
     */
    fun onSensorStatusChanged(status: SensorStatus)
    
    /**
     * Called when sensor error occurs
     */
    fun onSensorError(error: String)
}