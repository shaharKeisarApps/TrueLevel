package com.keisardev.truelevel.di

import android.content.Context
import com.keisardev.truelevel.domain.repositories.PlatformSensorManager
import com.keisardev.truelevel.domain.repositories.SensorManager

/**
 * Android-specific dependency initialization
 */
object PlatformModule {
    
    /**
     * Initialize the app with Android-specific dependencies
     */
    fun initialize(context: Context) {
        val sensorManager: SensorManager = PlatformSensorManager(context)
        
        AppModule.initialize(
            sensorManager = sensorManager,
            settingsRepository = null // Will be implemented later
        )
    }
}