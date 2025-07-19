package com.keisardev.truelevel.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keisardev.truelevel.domain.models.*
import com.keisardev.truelevel.domain.repositories.SensorManager
import com.keisardev.truelevel.presentation.state.LevelIntent
import com.keisardev.truelevel.presentation.state.LevelStateReducer
import com.keisardev.truelevel.presentation.state.LevelUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for level measurement screen with sensor integration
 */
class LevelViewModel(
    private val sensorManager: SensorManager,
    private val settingsRepository: com.keisardev.truelevel.domain.repositories.SettingsRepository? = null
) : ViewModel() {
    
    private val measurementProcessor = MeasurementProcessor()
    
    // Internal mutable state
    private val _uiState = MutableStateFlow(LevelStateReducer.createInitialState())
    
    // Public read-only state
    val uiState: StateFlow<LevelUiState> = _uiState.asStateFlow()
    
    // Sensor data collection job
    private var sensorJob: Job? = null
    
    // Battery optimization timer
    private var batteryOptimizationJob: Job? = null
    
    init {
        // Initialize sensor status
        checkSensorAvailability()
        
        // Load user preferences
        loadUserPreferences()
    }
    
    /**
     * Handles user intents and updates state accordingly
     */
    fun handleIntent(intent: LevelIntent) {
        when (intent) {
            is LevelIntent.StartMeasurement -> startMeasurement()
            is LevelIntent.StopMeasurement -> stopMeasurement()
            is LevelIntent.RequestSensorPermission -> requestSensorPermission()
            is LevelIntent.StartCalibration -> startCalibration()
            else -> {
                // Handle other intents through state reducer
                val currentState = _uiState.value
                val newState = LevelStateReducer.reduce(currentState, intent)
                _uiState.value = LevelStateReducer.validateState(newState)
                
                // Handle side effects for specific intents
                handleSideEffects(intent, newState)
            }
        }
    }
    
    /**
     * Starts sensor measurements
     */
    private fun startMeasurement() {
        viewModelScope.launch {
            try {
                updateState(LevelIntent.UpdateSensorStatus(SensorStatus.INITIALIZING))
                
                if (!sensorManager.isAvailable()) {
                    updateState(LevelIntent.HandleError("Sensors not available"))
                    return@launch
                }
                
                // Start sensor data collection
                sensorJob?.cancel()
                sensorJob = viewModelScope.launch {
                    sensorManager.startSensorUpdates()
                        .catch { error ->
                            updateState(LevelIntent.HandleError("Sensor error: ${error.message}"))
                        }
                        .collect { sensorData ->
                            processSensorData(sensorData)
                        }
                }
                
                updateState(LevelIntent.UpdateSensorStatus(SensorStatus.AVAILABLE))
                
            } catch (e: Exception) {
                updateState(LevelIntent.HandleError("Failed to start measurements: ${e.message}"))
            }
        }
    }
    
    /**
     * Stops sensor measurements
     */
    private fun stopMeasurement() {
        viewModelScope.launch {
            try {
                sensorJob?.cancel()
                sensorJob = null
                sensorManager.stopSensorUpdates()
                updateState(LevelIntent.StopMeasurement)
            } catch (e: Exception) {
                updateState(LevelIntent.HandleError("Failed to stop measurements: ${e.message}"))
            }
        }
    }
    
    /**
     * Processes raw sensor data into level measurements
     */
    private fun processSensorData(sensorData: SensorData) {
        try {
            val currentState = _uiState.value
            
            // Skip processing if battery optimization is enabled and device is stationary
            if (currentState.batteryOptimizationEnabled && isDeviceStationary(sensorData)) {
                return
            }
            
            // Process sensor data with current calibration
            val measurement = measurementProcessor.processSensorData(
                sensorData = sensorData,
                calibrationData = currentState.calibrationOffset
            )
            
            // Update state with new measurement using optimized update method
            val newState = LevelStateReducer.updateMeasurement(currentState, measurement)
            _uiState.value = newState
            
        } catch (e: Exception) {
            updateState(LevelIntent.HandleError("Measurement processing error: ${e.message}"))
        }
    }
    
    /**
     * Checks if device is stationary for battery optimization
     */
    private fun isDeviceStationary(sensorData: SensorData): Boolean {
        val currentState = _uiState.value
        val currentMeasurement = currentState.currentMeasurement ?: return false
        
        // Consider device stationary if angles haven't changed significantly
        val angleThreshold = 0.1 // degrees
        val timeSinceLastUpdate = sensorData.timestamp - currentMeasurement.timestamp
        
        return timeSinceLastUpdate < 1000 && // Less than 1 second
                kotlin.math.abs(sensorData.accelerometerX) < angleThreshold &&
                kotlin.math.abs(sensorData.accelerometerY) < angleThreshold
    }
    
    /**
     * Requests sensor permissions
     */
    private fun requestSensorPermission() {
        viewModelScope.launch {
            try {
                val granted = sensorManager.requestPermissions()
                if (granted) {
                    checkSensorAvailability()
                } else {
                    updateState(LevelIntent.UpdateSensorStatus(SensorStatus.PERMISSION_DENIED))
                }
            } catch (e: Exception) {
                updateState(LevelIntent.HandleError("Permission request failed: ${e.message}"))
            }
        }
    }
    
    /**
     * Starts calibration process
     */
    private fun startCalibration() {
        viewModelScope.launch {
            try {
                val calibrationData = sensorManager.calibrate()
                updateState(LevelIntent.ApplyCalibration(calibrationData))
                
                // Reset measurement processor filter after calibration
                measurementProcessor.resetFilter()
                
            } catch (e: Exception) {
                updateState(LevelIntent.HandleError("Calibration failed: ${e.message}"))
            }
        }
    }
    
    /**
     * Checks sensor availability and updates status
     */
    private fun checkSensorAvailability() {
        viewModelScope.launch {
            try {
                val isAvailable = sensorManager.isAvailable()
                val status = if (isAvailable) {
                    SensorStatus.AVAILABLE
                } else {
                    SensorStatus.UNAVAILABLE
                }
                updateState(LevelIntent.UpdateSensorStatus(status))
            } catch (e: Exception) {
                updateState(LevelIntent.HandleError("Sensor check failed: ${e.message}"))
            }
        }
    }
    
    /**
     * Handles side effects for specific intents
     */
    private fun handleSideEffects(intent: LevelIntent, newState: LevelUiState) {
        when (intent) {
            is LevelIntent.SwitchMode -> {
                // Reset measurement processor when switching modes
                measurementProcessor.resetFilter()
                
                // Save mode preference if requested
                if (intent.savePreference) {
                    saveModePreference(intent.mode)
                }
            }
            
            is LevelIntent.ToggleBatteryOptimization -> {
                if (newState.batteryOptimizationEnabled) {
                    enableBatteryOptimization()
                } else {
                    disableBatteryOptimization()
                }
            }
            
            is LevelIntent.ApplyCalibration -> {
                // Reset filter after calibration change
                measurementProcessor.resetFilter()
            }
            
            else -> { /* No side effects needed */ }
        }
    }
    
    /**
     * Enables battery optimization features
     */
    private fun enableBatteryOptimization() {
        measurementProcessor.setFilterStrength(FilterStrength.HIGH)
        
        // Start battery optimization monitoring
        batteryOptimizationJob?.cancel()
        batteryOptimizationJob = viewModelScope.launch {
            // Implementation for adaptive sensor polling would go here
            // This is a placeholder for future battery optimization features
        }
    }
    
    /**
     * Disables battery optimization features
     */
    private fun disableBatteryOptimization() {
        measurementProcessor.setFilterStrength(FilterStrength.MEDIUM)
        batteryOptimizationJob?.cancel()
        batteryOptimizationJob = null
    }
    
    /**
     * Updates state using the state reducer
     */
    private fun updateState(intent: LevelIntent) {
        val currentState = _uiState.value
        val newState = LevelStateReducer.reduce(currentState, intent)
        _uiState.value = LevelStateReducer.validateState(newState)
    }
    
    /**
     * Lifecycle management - stop sensors when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                sensorJob?.cancel()
                batteryOptimizationJob?.cancel()
                sensorManager.stopSensorUpdates()
            } catch (e: Exception) {
                // Log error but don't throw during cleanup
                println("Error during ViewModel cleanup: ${e.message}")
            }
        }
    }
    
    /**
     * Convenience method to get current state
     */
    fun getCurrentState(): LevelUiState = _uiState.value
    
    /**
     * Convenience method to check if measurements are active
     */
    fun isMeasuring(): Boolean {
        return sensorJob?.isActive == true && _uiState.value.sensorStatus == SensorStatus.AVAILABLE
    }
    
    /**
     * Loads user preferences from settings repository
     */
    private fun loadUserPreferences() {
        settingsRepository?.let { repository ->
            viewModelScope.launch {
                try {
                    val preferences = repository.getUserPreferences()
                    
                    // Update state with loaded preferences
                    val currentState = _uiState.value
                    _uiState.value = currentState.copy(
                        measurementMode = preferences.preferredMode,
                        batteryOptimizationEnabled = preferences.batteryOptimizationEnabled,
                        isLogging = preferences.isLoggingEnabled
                    )
                    
                    // Load calibration data
                    val calibration = repository.getCalibration()
                    if (calibration != null) {
                        updateState(LevelIntent.ApplyCalibration(calibration))
                    }
                    
                } catch (e: Exception) {
                    // Don't fail if preferences can't be loaded, use defaults
                    println("Failed to load user preferences: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Saves mode preference to settings repository
     */
    private fun saveModePreference(mode: MeasurementMode) {
        settingsRepository?.let { repository ->
            viewModelScope.launch {
                try {
                    repository.savePreferredMode(mode)
                } catch (e: Exception) {
                    println("Failed to save mode preference: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Saves all current preferences to settings repository
     */
    fun saveCurrentPreferences() {
        settingsRepository?.let { repository ->
            viewModelScope.launch {
                try {
                    val currentState = _uiState.value
                    val preferences = com.keisardev.truelevel.domain.repositories.UserPreferences(
                        preferredMode = currentState.measurementMode,
                        batteryOptimizationEnabled = currentState.batteryOptimizationEnabled,
                        isLoggingEnabled = currentState.isLogging
                    )
                    repository.saveUserPreferences(preferences)
                } catch (e: Exception) {
                    println("Failed to save preferences: ${e.message}")
                }
            }
        }
    }
}