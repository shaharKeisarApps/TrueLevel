package com.keisardev.truelevel.di

import com.keisardev.truelevel.domain.repositories.SensorManager
import com.keisardev.truelevel.domain.repositories.SettingsRepository
import com.keisardev.truelevel.presentation.viewmodels.LevelViewModel

/**
 * Simple dependency injection container for the TrueLevel app
 * This is a basic implementation that can be replaced with a proper DI framework later
 */
object AppModule {
    
    private var _sensorManager: SensorManager? = null
    private var _settingsRepository: SettingsRepository? = null
    private var _levelViewModel: LevelViewModel? = null
    
    /**
     * Initialize the app module with platform-specific dependencies
     */
    fun initialize(
        sensorManager: SensorManager,
        settingsRepository: SettingsRepository? = null
    ) {
        _sensorManager = sensorManager
        _settingsRepository = settingsRepository
        _levelViewModel = null // Reset ViewModel when dependencies change
    }
    
    /**
     * Get the sensor manager instance
     */
    fun getSensorManager(): SensorManager {
        return _sensorManager ?: throw IllegalStateException(
            "AppModule not initialized. Call initialize() first."
        )
    }
    
    /**
     * Get the settings repository instance
     */
    fun getSettingsRepository(): SettingsRepository? {
        return _settingsRepository
    }
    
    /**
     * Get the level view model instance (singleton)
     */
    fun getLevelViewModel(): LevelViewModel {
        return _levelViewModel ?: run {
            val viewModel = LevelViewModel(
                sensorManager = getSensorManager(),
                settingsRepository = getSettingsRepository()
            )
            _levelViewModel = viewModel
            viewModel
        }
    }
    
    /**
     * Check if the app module is initialized
     */
    fun isInitialized(): Boolean {
        return _sensorManager != null
    }
    
    /**
     * Clear all dependencies (useful for testing)
     */
    fun clear() {
        _levelViewModel = null
        _sensorManager = null
        _settingsRepository = null
    }
}