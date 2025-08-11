@file:OptIn(ExperimentalTime::class)

package com.keisardev.truelevel.domain.repositories

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager as AndroidSensorManager
import com.keisardev.truelevel.domain.models.CalibrationData
import com.keisardev.truelevel.domain.models.DeviceOrientation
import com.keisardev.truelevel.domain.models.SensorAccuracy
import com.keisardev.truelevel.domain.models.SensorData
import com.keisardev.truelevel.domain.models.SensorStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.ExperimentalTime

/**
 * Android-specific sensor manager implementation using Android Sensor API
 */
class PlatformSensorManager() : SensorManager {
    
    private var context: Context? = null
    
    constructor(context: Context) : this() {
        this.context = context
    }
    
    private val androidSensorManager: AndroidSensorManager?
        get() = context?.getSystemService(Context.SENSOR_SERVICE) as? AndroidSensorManager
    
    private val accelerometer: Sensor?
        get() = androidSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private val gyroscope: Sensor?
        get() = androidSensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    
    private var currentAccuracy = SensorAccuracy.MEDIUM
    private var isListening = false
    
    override fun startSensorUpdates(): Flow<SensorData> = callbackFlow {
        val sensorEventListener = object : SensorEventListener {
            private var accelerometerData: FloatArray? = null
            private var gyroscopeData: FloatArray? = null
            
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelerometerData = event.values.clone()
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gyroscopeData = event.values.clone()
                    }
                }
                
                // Send combined sensor data when accelerometer data is available
                accelerometerData?.let { accel ->
                    val sensorData = SensorData(
                        accelerometerX = accel[0],
                        accelerometerY = accel[1],
                        accelerometerZ = accel[2],
                        gyroscopeX = gyroscopeData?.get(0),
                        gyroscopeY = gyroscopeData?.get(1),
                        gyroscopeZ = gyroscopeData?.get(2),
                        timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds(),
                        accuracy = currentAccuracy
                    )
                    trySend(sensorData)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                currentAccuracy = when (accuracy) {
                    AndroidSensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
                    AndroidSensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
                    AndroidSensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
                    AndroidSensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
                    else -> SensorAccuracy.MEDIUM
                }
            }
        }
        
        // Register accelerometer listener
        accelerometer?.let { sensor ->
            androidSensorManager?.registerListener(
                sensorEventListener,
                sensor,
                AndroidSensorManager.SENSOR_DELAY_GAME
            )
        }
        
        // Register gyroscope listener if available
        gyroscope?.let { sensor ->
            androidSensorManager?.registerListener(
                sensorEventListener,
                sensor,
                AndroidSensorManager.SENSOR_DELAY_GAME
            )
        }
        
        isListening = true
        
        awaitClose {
            androidSensorManager?.unregisterListener(sensorEventListener)
            isListening = false
        }
    }
    
    override suspend fun stopSensorUpdates() {
        // Flow cleanup is handled in awaitClose
        isListening = false
    }
    
    override suspend fun isAvailable(): Boolean {
        return accelerometer != null
    }
    
    override suspend fun getCurrentAccuracy(): SensorAccuracy {
        return currentAccuracy
    }
    
    override suspend fun getSensorStatus(): SensorStatus {
        return when {
            accelerometer == null -> SensorStatus.UNAVAILABLE
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
            timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds(),
            deviceOrientation = DeviceOrientation.PORTRAIT
        )
    }
    
    override suspend fun isGyroscopeAvailable(): Boolean {
        return gyroscope != null
    }
    
    override suspend fun isAccelerometerAvailable(): Boolean {
        return accelerometer != null
    }
    
    override suspend fun requestPermissions(): Boolean {
        // Android sensors don't require runtime permissions for basic accelerometer/gyroscope
        // This would be used for other sensors that might require permissions
        return true
    }
}