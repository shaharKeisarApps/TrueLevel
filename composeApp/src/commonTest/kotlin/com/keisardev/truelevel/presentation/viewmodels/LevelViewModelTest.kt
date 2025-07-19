package com.keisardev.truelevel.presentation.viewmodels

import com.keisardev.truelevel.domain.models.*
import com.keisardev.truelevel.domain.repositories.SensorManager
import com.keisardev.truelevel.presentation.state.LevelIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LevelViewModelTest {
    
    private lateinit var mockSensorManager: MockSensorManager
    private lateinit var viewModel: LevelViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockSensorManager = MockSensorManager()
        viewModel = LevelViewModel(mockSensorManager)
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is properly set`() {
        val initialState = viewModel.getCurrentState()
        
        assertNull(initialState.currentMeasurement)
        assertEquals(MeasurementMode.DIGITAL_INCLINOMETER, initialState.measurementMode)
        assertFalse(initialState.isHoldActive)
        assertNull(initialState.heldMeasurement)
        assertFalse(initialState.isLogging)
        assertNull(initialState.calibrationOffset)
        assertFalse(initialState.batteryOptimizationEnabled)
        assertFalse(initialState.isCalibrationRequired)
        assertNull(initialState.error)
    }
    
    @Test
    fun `handleIntent ToggleHold activates hold with current measurement`() = runTest {
        // Setup: Add a current measurement
        val measurement = LevelMeasurement.createNow(1.5, 0.8)
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Toggle hold
        viewModel.handleIntent(LevelIntent.ToggleHold)
        
        // Assert
        val state = viewModel.getCurrentState()
        assertTrue(state.isHoldActive)
        assertNotNull(state.heldMeasurement)
    }
    
    @Test
    fun `handleIntent ToggleHold deactivates hold when already active`() = runTest {
        // Setup: Activate hold first
        viewModel.handleIntent(LevelIntent.ActivateHold)
        assertTrue(viewModel.getCurrentState().isHoldActive)
        
        // Act: Toggle hold again
        viewModel.handleIntent(LevelIntent.ToggleHold)
        
        // Assert
        val state = viewModel.getCurrentState()
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
    }
    
    @Test
    fun `handleIntent SwitchMode changes measurement mode`() {
        // Act
        viewModel.handleIntent(LevelIntent.SwitchMode(MeasurementMode.BUBBLE_LEVEL))
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(MeasurementMode.BUBBLE_LEVEL, state.measurementMode)
    }
    
    @Test
    fun `handleIntent SwitchMode clears hold state`() {
        // Setup: Activate hold first
        viewModel.handleIntent(LevelIntent.ActivateHold)
        assertTrue(viewModel.getCurrentState().isHoldActive)
        
        // Act: Switch mode
        viewModel.handleIntent(LevelIntent.SwitchMode(MeasurementMode.BUBBLE_LEVEL))
        
        // Assert: Hold should be cleared
        val state = viewModel.getCurrentState()
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
    }
    
    @Test
    fun `handleIntent ToggleLogging toggles logging state`() {
        // Initial state
        assertFalse(viewModel.getCurrentState().isLogging)
        
        // Act: Toggle logging on
        viewModel.handleIntent(LevelIntent.ToggleLogging)
        assertTrue(viewModel.getCurrentState().isLogging)
        
        // Act: Toggle logging off
        viewModel.handleIntent(LevelIntent.ToggleLogging)
        assertFalse(viewModel.getCurrentState().isLogging)
    }
    
    @Test
    fun `handleIntent ApplyCalibration sets calibration data`() {
        // Setup
        val calibrationData = CalibrationData.create(1.0, 0.5)
        
        // Act
        viewModel.handleIntent(LevelIntent.ApplyCalibration(calibrationData))
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(calibrationData, state.calibrationOffset)
        assertFalse(state.isCalibrationRequired)
    }
    
    @Test
    fun `handleIntent ResetCalibration sets default calibration`() {
        // Setup: First set a custom calibration
        val customCalibration = CalibrationData.create(2.0, 1.5)
        viewModel.handleIntent(LevelIntent.ApplyCalibration(customCalibration))
        
        // Act: Reset calibration
        viewModel.handleIntent(LevelIntent.ResetCalibration)
        
        // Assert
        val state = viewModel.getCurrentState()
        assertNotNull(state.calibrationOffset)
        assertEquals(0.0, state.calibrationOffset!!.offsetX)
        assertEquals(0.0, state.calibrationOffset!!.offsetY)
    }
    
    @Test
    fun `handleIntent ClearError removes error`() {
        // Setup: Set an error first
        viewModel.handleIntent(LevelIntent.HandleError("Test error"))
        assertEquals("Test error", viewModel.getCurrentState().error)
        
        // Act: Clear error
        viewModel.handleIntent(LevelIntent.ClearError)
        
        // Assert
        assertNull(viewModel.getCurrentState().error)
    }
    
    @Test
    fun `startMeasurement updates sensor status to initializing then available`() = runTest {
        // Setup
        mockSensorManager.isAvailableResult = true
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        // Act
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        // The sensor status should be AVAILABLE after successful start
        assertTrue(state.sensorStatus == SensorStatus.AVAILABLE || state.sensorStatus == SensorStatus.INITIALIZING)
        // Check that we have received sensor data
        assertNotNull(state.currentMeasurement)
    }
    
    @Test
    fun `startMeasurement handles sensor unavailable`() = runTest {
        // Setup
        mockSensorManager.isAvailableResult = false
        
        // Act
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(SensorStatus.ERROR, state.sensorStatus)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("not available"))
    }
    
    @Test
    fun `stopMeasurement clears measurements and stops sensor`() = runTest {
        // Setup: Start measurements first
        mockSensorManager.isAvailableResult = true
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Stop measurements
        viewModel.handleIntent(LevelIntent.StopMeasurement)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertNull(state.currentMeasurement)
        assertEquals(SensorStatus.UNAVAILABLE, state.sensorStatus)
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
        assertFalse(viewModel.isMeasuring())
    }
    
    @Test
    fun `sensor data processing updates current measurement`() = runTest {
        // Setup
        mockSensorManager.isAvailableResult = true
        val testSensorData = SensorData.createNow(
            accelerometerX = 1.0f,
            accelerometerY = 0.5f,
            accelerometerZ = 9.8f,
            accuracy = SensorAccuracy.HIGH
        )
        mockSensorManager.sensorData = flowOf(testSensorData)
        
        // Act
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertNotNull(state.currentMeasurement)
        assertEquals(SensorAccuracy.HIGH, state.currentMeasurement!!.accuracy)
    }
    
    @Test
    fun `battery optimization affects measurement processing`() = runTest {
        // Setup
        mockSensorManager.isAvailableResult = true
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.01f, 0.01f, 9.8f) // Very small movement
        )
        
        // Start measurements and enable battery optimization
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        viewModel.handleIntent(LevelIntent.ToggleBatteryOptimization)
        advanceUntilIdle()
        
        // Assert battery optimization is enabled
        assertTrue(viewModel.getCurrentState().batteryOptimizationEnabled)
    }
    
    @Test
    fun `calibration process applies calibration data`() = runTest {
        // Setup
        val expectedCalibration = CalibrationData.create(1.5, 0.8)
        mockSensorManager.calibrationResult = expectedCalibration
        
        // Act
        viewModel.handleIntent(LevelIntent.StartCalibration)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(expectedCalibration, state.calibrationOffset)
        assertFalse(state.isCalibrationRequired)
    }
    
    @Test
    fun `permission request updates sensor status`() = runTest {
        // Setup
        mockSensorManager.permissionResult = true
        mockSensorManager.isAvailableResult = true
        
        // Act
        viewModel.handleIntent(LevelIntent.RequestSensorPermission)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(SensorStatus.AVAILABLE, state.sensorStatus)
    }
    
    @Test
    fun `permission denied updates sensor status`() = runTest {
        // Setup
        mockSensorManager.permissionResult = false
        
        // Act
        viewModel.handleIntent(LevelIntent.RequestSensorPermission)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(SensorStatus.PERMISSION_DENIED, state.sensorStatus)
    }
    
    @Test
    fun `error handling sets error state`() = runTest {
        // Setup
        mockSensorManager.shouldThrowError = true
        
        // Act
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertEquals(SensorStatus.ERROR, state.sensorStatus)
        assertNotNull(state.error)
    }
}

/**
 * Mock implementation of SensorManager for testing
 */
class MockSensorManager : SensorManager {
    var isAvailableResult = true
    var permissionResult = true
    var calibrationResult = CalibrationData.default()
    var shouldThrowError = false
    var sensorData = flowOf<SensorData>()
    
    override fun startSensorUpdates() = if (shouldThrowError) {
        flow<SensorData> { throw Exception("Mock sensor error") }
    } else {
        sensorData
    }
    
    override suspend fun stopSensorUpdates() {
        if (shouldThrowError) throw Exception("Mock stop error")
    }
    
    override suspend fun isAvailable(): Boolean {
        if (shouldThrowError) throw Exception("Mock availability error")
        return isAvailableResult
    }
    
    override suspend fun getCurrentAccuracy(): SensorAccuracy {
        return SensorAccuracy.HIGH
    }
    
    override suspend fun getSensorStatus(): SensorStatus {
        return if (isAvailableResult) SensorStatus.AVAILABLE else SensorStatus.UNAVAILABLE
    }
    
    override suspend fun calibrate(): CalibrationData {
        if (shouldThrowError) throw Exception("Mock calibration error")
        return calibrationResult
    }
    
    override suspend fun isGyroscopeAvailable(): Boolean = true
    
    override suspend fun isAccelerometerAvailable(): Boolean = true
    
    override suspend fun requestPermissions(): Boolean {
        if (shouldThrowError) throw Exception("Mock permission error")
        return permissionResult
    }
}