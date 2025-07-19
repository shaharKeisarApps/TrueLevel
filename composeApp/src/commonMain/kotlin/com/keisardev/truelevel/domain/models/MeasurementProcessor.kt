package com.keisardev.truelevel.domain.models

import kotlin.math.*

/**
 * Core measurement processing engine for converting raw sensor data to level measurements
 */
class MeasurementProcessor {
    
    // Low-pass filter state
    private var filteredAccelX: Float = 0f
    private var filteredAccelY: Float = 0f
    private var filteredAccelZ: Float = 0f
    private var isFilterInitialized = false
    
    // Filter coefficient for low-pass filter (0.0 = no filtering, 1.0 = maximum filtering)
    private var filterAlpha = 0.8f
    
    /**
     * Processes raw sensor data into a level measurement
     */
    fun processSensorData(
        sensorData: SensorData,
        calibrationData: CalibrationData? = null
    ): LevelMeasurement {
        // Apply low-pass filter to reduce noise
        val filteredData = applyLowPassFilter(sensorData)
        
        // Calculate pitch and roll angles from filtered accelerometer data
        val rawPitch = calculatePitch(
            filteredData.accelerometerX,
            filteredData.accelerometerY,
            filteredData.accelerometerZ
        )
        val rawRoll = calculateRoll(
            filteredData.accelerometerX,
            filteredData.accelerometerY,
            filteredData.accelerometerZ
        )
        
        // Apply calibration offsets if available
        val calibratedPitch = calibrationData?.let { 
            applyCalibrationOffset(rawPitch, it.offsetX) 
        } ?: rawPitch
        
        val calibratedRoll = calibrationData?.let { 
            applyCalibrationOffset(rawRoll, it.offsetY) 
        } ?: rawRoll
        
        // Normalize angles to -180 to 180 range
        val normalizedPitch = normalizeAngle(calibratedPitch)
        val normalizedRoll = normalizeAngle(calibratedRoll)
        
        // Create measurement with calculated level status
        return LevelMeasurement.create(
            angleX = normalizedPitch,
            angleY = normalizedRoll,
            timestamp = sensorData.timestamp,
            accuracy = sensorData.accuracy
        )
    }
    
    /**
     * Applies low-pass filter to sensor data to reduce noise
     */
    private fun applyLowPassFilter(sensorData: SensorData): SensorData {
        if (!isFilterInitialized) {
            // Initialize filter with first reading
            filteredAccelX = sensorData.accelerometerX
            filteredAccelY = sensorData.accelerometerY
            filteredAccelZ = sensorData.accelerometerZ
            isFilterInitialized = true
            return sensorData
        }
        
        // Apply low-pass filter: filtered = alpha * filtered + (1 - alpha) * current
        filteredAccelX = filterAlpha * filteredAccelX + (1 - filterAlpha) * sensorData.accelerometerX
        filteredAccelY = filterAlpha * filteredAccelY + (1 - filterAlpha) * sensorData.accelerometerY
        filteredAccelZ = filterAlpha * filteredAccelZ + (1 - filterAlpha) * sensorData.accelerometerZ
        
        return sensorData.copy(
            accelerometerX = filteredAccelX,
            accelerometerY = filteredAccelY,
            accelerometerZ = filteredAccelZ
        )
    }
    
    /**
     * Calculates pitch angle from accelerometer data
     * Pitch is rotation around X-axis
     */
    private fun calculatePitch(x: Float, y: Float, z: Float): Double {
        return atan2(x.toDouble(), sqrt(y * y + z * z).toDouble()) * 180.0 / PI
    }
    
    /**
     * Calculates roll angle from accelerometer data
     * Roll is rotation around Y-axis
     */
    private fun calculateRoll(x: Float, y: Float, z: Float): Double {
        return atan2(y.toDouble(), sqrt(x * x + z * z).toDouble()) * 180.0 / PI
    }
    
    /**
     * Applies calibration offset to an angle measurement
     */
    private fun applyCalibrationOffset(angle: Double, offset: Double): Double {
        return angle - offset
    }
    
    /**
     * Normalizes angle to be within -180 to 180 degrees
     */
    private fun normalizeAngle(angle: Double): Double {
        var normalized = angle % 360.0
        if (normalized > 180.0) {
            normalized -= 360.0
        } else if (normalized < -180.0) {
            normalized += 360.0
        }
        return normalized
    }
    
    /**
     * Resets the low-pass filter state
     */
    fun resetFilter() {
        isFilterInitialized = false
        filteredAccelX = 0f
        filteredAccelY = 0f
        filteredAccelZ = 0f
    }
    
    /**
     * Updates filter coefficient for different noise reduction levels
     */
    fun setFilterStrength(strength: FilterStrength) {
        val newAlpha = when (strength) {
            FilterStrength.NONE -> 0.0f
            FilterStrength.LOW -> 0.3f
            FilterStrength.MEDIUM -> 0.8f
            FilterStrength.HIGH -> 0.95f
        }
        // Reset filter when changing strength to avoid artifacts
        if (abs(newAlpha - filterAlpha) > 0.1f) {
            resetFilter()
        }
        filterAlpha = newAlpha
    }
    
    companion object {
        /**
         * Creates a calibration data from current sensor reading
         * This assumes the device is currently level
         */
        fun createCalibrationFromLevel(sensorData: SensorData): CalibrationData {
            val pitch = atan2(
                sensorData.accelerometerX.toDouble(),
                sqrt(sensorData.accelerometerY * sensorData.accelerometerY + 
                     sensorData.accelerometerZ * sensorData.accelerometerZ).toDouble()
            ) * 180.0 / PI
            
            val roll = atan2(
                sensorData.accelerometerY.toDouble(),
                sqrt(sensorData.accelerometerX * sensorData.accelerometerX + 
                     sensorData.accelerometerZ * sensorData.accelerometerZ).toDouble()
            ) * 180.0 / PI
            
            return CalibrationData.create(
                offsetX = pitch,
                offsetY = roll
            )
        }
    }
}

/**
 * Filter strength levels for noise reduction
 */
enum class FilterStrength {
    NONE,    // No filtering
    LOW,     // Light filtering
    MEDIUM,  // Moderate filtering (default)
    HIGH     // Heavy filtering
}