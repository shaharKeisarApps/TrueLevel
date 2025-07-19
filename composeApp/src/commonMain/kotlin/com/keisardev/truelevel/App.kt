package com.keisardev.truelevel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.keisardev.truelevel.di.AppModule
import com.keisardev.truelevel.navigation.LevelNavigation
import com.keisardev.truelevel.ui.theme.LevelTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    LevelTheme {
        val navController = rememberNavController()
        
        // Initialize the app with a mock sensor manager for preview
        LaunchedEffect(Unit) {
            if (!AppModule.isInitialized()) {
                AppModule.initialize(
                    sensorManager = MockSensorManager(),
                    settingsRepository = null
                )
            }
        }
        
        LevelNavigation(
            navController = navController,
            viewModel = AppModule.getLevelViewModel(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Mock sensor manager for preview and testing
 */
private class MockSensorManager : com.keisardev.truelevel.domain.repositories.SensorManager {
    override fun startSensorUpdates(): kotlinx.coroutines.flow.Flow<com.keisardev.truelevel.domain.models.SensorData> = 
        kotlinx.coroutines.flow.flowOf()
    override suspend fun stopSensorUpdates() {}
    override suspend fun isAvailable() = true
    override suspend fun getCurrentAccuracy() = com.keisardev.truelevel.domain.models.SensorAccuracy.HIGH
    override suspend fun getSensorStatus() = com.keisardev.truelevel.domain.models.SensorStatus.AVAILABLE
    override suspend fun calibrate() = com.keisardev.truelevel.domain.models.CalibrationData.default()
    override suspend fun isGyroscopeAvailable() = true
    override suspend fun isAccelerometerAvailable() = true
    override suspend fun requestPermissions() = true
}