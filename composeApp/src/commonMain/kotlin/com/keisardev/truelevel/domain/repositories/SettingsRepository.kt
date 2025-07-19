package com.keisardev.truelevel.domain.repositories

import com.keisardev.truelevel.domain.models.CalibrationData
import com.keisardev.truelevel.domain.models.MeasurementMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings and preferences
 */
interface SettingsRepository {
    
    /**
     * Save calibration data
     */
    suspend fun saveCalibration(calibration: CalibrationData)
    
    /**
     * Get saved calibration data
     */
    suspend fun getCalibration(): CalibrationData?
    
    /**
     * Clear calibration data
     */
    suspend fun clearCalibration()
    
    /**
     * Save preferred measurement mode
     */
    suspend fun savePreferredMode(mode: MeasurementMode)
    
    /**
     * Get preferred measurement mode
     */
    suspend fun getPreferredMode(): MeasurementMode
    
    /**
     * Observe preferred measurement mode changes
     */
    fun observePreferredMode(): Flow<MeasurementMode>
    
    /**
     * Save battery optimization preference
     */
    suspend fun saveBatteryOptimization(enabled: Boolean)
    
    /**
     * Get battery optimization preference
     */
    suspend fun getBatteryOptimization(): Boolean
    
    /**
     * Save theme preferences
     */
    suspend fun saveThemeMode(isDarkMode: Boolean)
    
    /**
     * Get theme preferences
     */
    suspend fun getThemeMode(): Boolean
    
    /**
     * Save high contrast preference
     */
    suspend fun saveHighContrast(enabled: Boolean)
    
    /**
     * Get high contrast preference
     */
    suspend fun getHighContrast(): Boolean
    
    /**
     * Save all user preferences
     */
    suspend fun saveUserPreferences(preferences: UserPreferences)
    
    /**
     * Get all user preferences
     */
    suspend fun getUserPreferences(): UserPreferences
    
    /**
     * Observe user preferences changes
     */
    fun observeUserPreferences(): Flow<UserPreferences>
    
    /**
     * Clear all settings and preferences
     */
    suspend fun clearAllSettings()
}

/**
 * Data class representing user preferences
 */
data class UserPreferences(
    val preferredMode: MeasurementMode = MeasurementMode.DIGITAL_INCLINOMETER,
    val batteryOptimizationEnabled: Boolean = false,
    val isDarkMode: Boolean = false,
    val isHighContrastEnabled: Boolean = false,
    val isLoggingEnabled: Boolean = false,
    val autoCalibrationEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val voiceAnnouncementsEnabled: Boolean = false
)