package com.keisardev.truelevel.di

import com.keisardev.truelevel.domain.repositories.PlatformSensorManager
import com.keisardev.truelevel.domain.repositories.SensorManager

/**
 * iOS-specific dependency initialization
 */
object PlatformModule {
    
    /**
     * Initialize the app with iOS-specific dependencies
     */
    fun initialize() {
        val sensorManager: SensorManager = PlatformSensorManager()
        
        AppModule.initialize(
            sensorManager = sensorManager,
            settingsRepository = null // Will be implemented later
        )
    }
}