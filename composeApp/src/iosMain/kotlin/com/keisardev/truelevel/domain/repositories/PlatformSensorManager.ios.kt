package com.keisardev.truelevel.domain.repositories

import com.keisardev.truelevel.domain.models.CalibrationData
import com.keisardev.truelevel.domain.models.DeviceOrientation
import com.keisardev.truelevel.domain.models.SensorAccuracy
import com.keisardev.truelevel.domain.models.SensorData
import com.keisardev.truelevel.domain.models.SensorStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.datetime.Clock
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

/**
 * iOS-specific sensor manager implementation using Core Motion
 */
@OptIn(ExperimentalForeignApi::class)
class PlatformSensorManager : SensorManager {
    
    private val motionManager = CMMotionManager()
    private var currentAccuracy = SensorAccuracy.MEDIUM
    private var isListening = false
    
    override fun startSensorUpdates(): Flow<SensorData> = callbackFlow {
        if (!motionManager.deviceMotionAvailable) {
            close()
            return@callbackFlow
        }
        
        motionManager.deviceMotionUpdateInterval = 1.0 / 60.0 // 60 Hz
        
        motionManager.startDeviceMotionUpdatesToQueue(
            NSOperationQueue.mainQueue
        ) { motion, error ->
            if (error != null) {
                // Handle error
                return@startDeviceMotionUpdatesToQueue
            }
            
            motion?.let { deviceMotion ->
                val gravity = deviceMotion.gravity
                val rotationRate = deviceMotion.rotationRate
                
                val sensorData = SensorData(
                    accelerometerX = gravity.useContents { x }.toFloat(),
                    accelerometerY = gravity.useContents { y }.toFloat(),
                    accelerometerZ = gravity.useContents { z }.toFloat(),
                    gyroscopeX = rotationRate.useContents { x }.toFloat(),
                    gyroscopeY = rotationRate.useContents { y }.toFloat(),
                    gyroscopeZ = rotationRate.useContents { z }.toFloat(),
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    accuracy = currentAccuracy
                )
                trySend(sensorData)
            }
        }
        
        isListening = true
        
        awaitClose {
            motionManager.stopDeviceMotionUpdates()
            isListening = false
        }
    }
    
    override suspend fun stopSensorUpdates() {
        motionManager.stopDeviceMotionUpdates()
        isListening = false
    }
    
    override suspend fun isAvailable(): Boolean {
        return motionManager.deviceMotionAvailable
    }
    
    override suspend fun getCurrentAccuracy(): SensorAccuracy {
        return currentAccuracy
    }
    
    override suspend fun getSensorStatus(): SensorStatus {
        return when {
            !motionManager.deviceMotionAvailable -> SensorStatus.UNAVAILABLE
            isListening -> SensorStatus.AVAILABLE
            else -> SensorStatus.AVAILABLE
        }
    }
    
    override suspend fun calibrate(): CalibrationData {
        // Basic calibration implementation
        // In a real implementation, this would collect samples and calculate offsets
        return CalibrationData(
            offsetX = 0.0,
            offsetY = 0.0,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            deviceOrientation = DeviceOrientation.PORTRAIT
        )
    }
    
    override suspend fun isGyroscopeAvailable(): Boolean {
        return motionManager.gyroAvailable
    }
    
    override suspend fun isAccelerometerAvailable(): Boolean {
        return motionManager.accelerometerAvailable
    }
    
    override suspend fun requestPermissions(): Boolean {
        // iOS Core Motion doesn't require explicit permissions for basic motion data
        // Motion & Fitness permission is handled automatically by the system
        return true
    }
}