package com.keisardev.truelevel.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.keisardev.truelevel.domain.models.LevelMeasurement
import com.keisardev.truelevel.domain.models.MeasurementMode
import com.keisardev.truelevel.domain.models.SensorAccuracy
import com.keisardev.truelevel.domain.models.SensorStatus
import com.keisardev.truelevel.presentation.state.LevelUiState
import com.keisardev.truelevel.presentation.viewmodels.LevelViewModel
import com.keisardev.truelevel.ui.theme.LevelTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration tests for MainMeasurementScreen mode switching functionality
 */
class MainMeasurementScreenTest {
    
    private val composeTestRule = createComposeRule()
    
    @Test
    fun mainMeasurementScreen_displaysCorrectModeContent() = runTest {
        // Given
        val mockViewModel = createMockViewModel()
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("TrueLevel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Digital").assertIsDisplayed()
    }
    
    @Test
    fun mainMeasurementScreen_switchesModeOnTabClick() = runTest {
        // Given
        val mockViewModel = createMockViewModel()
        var capturedMode: MeasurementMode? = null
        
        // Mock the intent handling
        mockViewModel.onIntentHandler = { intent ->
            if (intent is com.keisardev.truelevel.presentation.state.LevelIntent.SwitchMode) {
                capturedMode = intent.mode
            }
        }
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Click on bubble mode tab
        composeTestRule.onNodeWithText("Bubble").performClick()
        
        // Then
        assertEquals(MeasurementMode.BUBBLE_LEVEL, capturedMode)
    }
    
    @Test
    fun mainMeasurementScreen_showsErrorState() = runTest {
        // Given
        val mockViewModel = createMockViewModel(
            initialState = LevelUiState(
                error = "Test error message",
                sensorStatus = SensorStatus.ERROR
            )
        )
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test error message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
    
    @Test
    fun mainMeasurementScreen_showsLoadingState() = runTest {
        // Given
        val mockViewModel = createMockViewModel(
            initialState = LevelUiState(
                sensorStatus = SensorStatus.INITIALIZING
            )
        )
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Initializing sensors...").assertIsDisplayed()
    }
    
    @Test
    fun mainMeasurementScreen_showsPermissionDeniedState() = runTest {
        // Given
        val mockViewModel = createMockViewModel(
            initialState = LevelUiState(
                sensorStatus = SensorStatus.PERMISSION_DENIED
            )
        )
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Sensor Permission Required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grant Permission").assertIsDisplayed()
    }
    
    @Test
    fun mainMeasurementScreen_showsSensorUnavailableState() = runTest {
        // Given
        val mockViewModel = createMockViewModel(
            initialState = LevelUiState(
                sensorStatus = SensorStatus.UNAVAILABLE
            )
        )
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Sensors Unavailable").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
    
    @Test
    fun mainMeasurementScreen_preservesStateOnModeSwitch() = runTest {
        // Given
        val testMeasurement = LevelMeasurement.createNow(
            angleX = 2.5,
            angleY = 1.2,
            accuracy = SensorAccuracy.HIGH
        )
        
        val mockViewModel = createMockViewModel(
            initialState = LevelUiState(
                currentMeasurement = testMeasurement,
                measurementMode = MeasurementMode.DIGITAL_INCLINOMETER,
                sensorStatus = SensorStatus.AVAILABLE
            )
        )
        
        // When
        composeTestRule.setContent {
            LevelTheme {
                MainMeasurementScreen(
                    viewModel = mockViewModel
                )
            }
        }
        
        // Switch to bubble mode
        composeTestRule.onNodeWithText("Bubble").performClick()
        
        // Then - measurement should still be available
        // This would be verified by checking that the bubble display shows the measurement
        composeTestRule.onNodeWithContentDescription("Bubble level display").assertIsDisplayed()
    }
    
    private fun createMockViewModel(
        initialState: LevelUiState = LevelUiState()
    ): MockLevelViewModel {
        return MockLevelViewModel(initialState)
    }
}

/**
 * Mock ViewModel for testing
 */
class MockLevelViewModel(
    initialState: LevelUiState
) : LevelViewModel(MockSensorManager()) {
    
    private val _uiState = MutableStateFlow(initialState)
    override val uiState = _uiState
    
    var onIntentHandler: ((com.keisardev.truelevel.presentation.state.LevelIntent) -> Unit)? = null
    
    override fun handleIntent(intent: com.keisardev.truelevel.presentation.state.LevelIntent) {
        onIntentHandler?.invoke(intent)
        
        // Update state based on intent for testing
        when (intent) {
            is com.keisardev.truelevel.presentation.state.LevelIntent.SwitchMode -> {
                _uiState.value = _uiState.value.copy(measurementMode = intent.mode)
            }
            is com.keisardev.truelevel.presentation.state.LevelIntent.ClearError -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            else -> {
                // Handle other intents as needed for testing
            }
        }
    }
}

/**
 * Mock SensorManager for testing
 */
class MockSensorManager : com.keisardev.truelevel.domain.repositories.SensorManager {
    override fun startSensorUpdates() = kotlinx.coroutines.flow.flowOf()
    override suspend fun stopSensorUpdates() {}
    override suspend fun isAvailable() = true
    override suspend fun getCurrentAccuracy() = SensorAccuracy.HIGH
    override suspend fun getSensorStatus() = SensorStatus.AVAILABLE
    override suspend fun calibrate() = com.keisardev.truelevel.domain.models.CalibrationData.default()
    override suspend fun isGyroscopeAvailable() = true
    override suspend fun isAccelerometerAvailable() = true
    override suspend fun requestPermissions() = true
}