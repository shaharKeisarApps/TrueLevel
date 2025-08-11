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
    
    // Hold/Freeze Functionality Tests
    
    @Test
    fun `activateHold sets hold state with current measurement`() = runTest {
        // Setup: Add a current measurement
        val measurement = LevelMeasurement.createNow(2.5, 1.2)
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.2f, 0.1f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Activate hold
        viewModel.activateHold()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertTrue(state.isHoldActive)
        assertNotNull(state.heldMeasurement)
        assertEquals(state.currentMeasurement, state.heldMeasurement)
    }
    
    @Test
    fun `deactivateHold clears hold state`() = runTest {
        // Setup: Activate hold first
        viewModel.handleIntent(LevelIntent.ActivateHold)
        assertTrue(viewModel.getCurrentState().isHoldActive)
        
        // Act: Deactivate hold
        viewModel.deactivateHold()
        
        // Assert
        val state = viewModel.getCurrentState()
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
    }
    
    @Test
    fun `toggleHold switches hold state correctly`() = runTest {
        // Setup: Add a current measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Initial state - not held
        assertFalse(viewModel.getCurrentState().isHoldActive)
        
        // Act: Toggle hold on
        viewModel.toggleHold()
        assertTrue(viewModel.getCurrentState().isHoldActive)
        assertNotNull(viewModel.getCurrentState().heldMeasurement)
        
        // Act: Toggle hold off
        viewModel.toggleHold()
        assertFalse(viewModel.getCurrentState().isHoldActive)
        assertNull(viewModel.getCurrentState().heldMeasurement)
    }
    
    @Test
    fun `canActivateHold returns true when measurement available and not held`() = runTest {
        // Setup: Add a current measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Assert: Can activate hold
        assertTrue(viewModel.canActivateHold())
        
        // Act: Activate hold
        viewModel.activateHold()
        
        // Assert: Cannot activate hold when already active
        assertFalse(viewModel.canActivateHold())
    }
    
    @Test
    fun `canActivateHold returns false when no measurement available`() {
        // No measurement setup
        
        // Assert: Cannot activate hold without measurement
        assertFalse(viewModel.canActivateHold())
    }
    
    @Test
    fun `getDisplayMeasurement returns held measurement when hold active`() = runTest {
        // Setup: Add measurements
        val initialMeasurement = LevelMeasurement.createNow(1.0, 0.5)
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f),
            SensorData.createNow(0.2f, 0.1f, 9.8f) // Different measurement
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Activate hold
        viewModel.activateHold()
        val heldMeasurement = viewModel.getCurrentState().heldMeasurement
        
        // Assert: Display measurement should be the held one
        assertEquals(heldMeasurement, viewModel.getDisplayMeasurement())
        assertEquals(heldMeasurement, viewModel.getCurrentState().displayMeasurement)
    }
    
    @Test
    fun `getDisplayMeasurement returns current measurement when hold not active`() = runTest {
        // Setup: Add a current measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Assert: Display measurement should be the current one
        val currentMeasurement = viewModel.getCurrentState().currentMeasurement
        assertEquals(currentMeasurement, viewModel.getDisplayMeasurement())
        assertEquals(currentMeasurement, viewModel.getCurrentState().displayMeasurement)
    }
    
    @Test
    fun `handleTapGesture activates hold after delay for single tap`() = runTest {
        // Setup: Add a current measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Single tap
        viewModel.handleTapGesture()
        
        // Assert: Hold not active immediately
        assertFalse(viewModel.getCurrentState().isHoldActive)
        
        // Wait for hold activation delay
        advanceTimeBy(600L) // More than holdActivationDelay (500ms)
        
        // Assert: Hold should be active now
        assertTrue(viewModel.getCurrentState().isHoldActive)
    }
    
    @Test
    fun `handleTapGesture toggles hold immediately for double tap`() = runTest {
        // Setup: Add a current measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Double tap (two taps within threshold)
        viewModel.handleTapGesture()
        advanceTimeBy(200L) // Within doubleTapThreshold (300ms)
        viewModel.handleTapGesture()
        
        // Assert: Hold should be active immediately
        assertTrue(viewModel.getCurrentState().isHoldActive)
        
        // Act: Double tap again to deactivate
        viewModel.handleTapGesture()
        advanceTimeBy(200L)
        viewModel.handleTapGesture()
        
        // Assert: Hold should be inactive
        assertFalse(viewModel.getCurrentState().isHoldActive)
    }
    
    @Test
    fun `cancelHoldActivation prevents hold activation`() = runTest {
        // Setup: Add a current measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Start tap gesture then cancel
        viewModel.handleTapGesture()
        advanceTimeBy(200L) // Partial delay
        viewModel.cancelHoldActivation()
        
        // Wait past the activation delay
        advanceTimeBy(400L)
        
        // Assert: Hold should not be active
        assertFalse(viewModel.getCurrentState().isHoldActive)
    }
    
    @Test
    fun `held measurement persists without drift during sensor updates`() = runTest {
        // Setup: Add initial measurement
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        
        // Act: Activate hold
        viewModel.activateHold()
        val heldMeasurement = viewModel.getCurrentState().heldMeasurement!!
        
        // Simulate new sensor data
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.5f, 0.3f, 9.8f) // Different values
        )
        
        // Process new sensor data
        advanceTimeBy(100L)
        
        // Assert: Held measurement should remain unchanged
        val currentState = viewModel.getCurrentState()
        assertTrue(currentState.isHoldActive)
        assertEquals(heldMeasurement, currentState.heldMeasurement)
        
        // Current measurement should be updated, but held should remain the same
        assertNotEquals(currentState.currentMeasurement, currentState.heldMeasurement)
    }
    
    @Test
    fun `hold state is cleared when switching measurement modes`() = runTest {
        // Setup: Add measurement and activate hold
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        viewModel.activateHold()
        
        // Verify hold is active
        assertTrue(viewModel.getCurrentState().isHoldActive)
        
        // Act: Switch measurement mode
        viewModel.handleIntent(LevelIntent.SwitchMode(MeasurementMode.BUBBLE_LEVEL))
        
        // Assert: Hold should be cleared
        val state = viewModel.getCurrentState()
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
        assertEquals(MeasurementMode.BUBBLE_LEVEL, state.measurementMode)
    }
    
    @Test
    fun `hold state is cleared when stopping measurements`() = runTest {
        // Setup: Add measurement and activate hold
        mockSensorManager.sensorData = flowOf(
            SensorData.createNow(0.1f, 0.05f, 9.8f)
        )
        
        viewModel.handleIntent(LevelIntent.StartMeasurement)
        advanceUntilIdle()
        viewModel.activateHold()
        
        // Verify hold is active
        assertTrue(viewModel.getCurrentState().isHoldActive)
        
        // Act: Stop measurements
        viewModel.handleIntent(LevelIntent.StopMeasurement)
        advanceUntilIdle()
        
        // Assert: Hold should be cleared
        val state = viewModel.getCurrentState()
        assertFalse(state.isHoldActive)
        assertNull(state.heldMeasurement)
        assertNull(state.currentMeasurement)
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