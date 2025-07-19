package com.keisardev.truelevel.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.keisardev.truelevel.domain.models.MeasurementMode
import com.keisardev.truelevel.domain.models.SensorStatus
import com.keisardev.truelevel.presentation.state.LevelIntent
import com.keisardev.truelevel.presentation.state.LevelUiState
import com.keisardev.truelevel.presentation.viewmodels.LevelViewModel
import com.keisardev.truelevel.ui.components.BubbleLevelDisplay
import com.keisardev.truelevel.ui.components.DigitalInclinometerDisplay
import com.keisardev.truelevel.ui.components.ModeSelector
import com.keisardev.truelevel.ui.components.ModeTransition
import com.keisardev.truelevel.ui.theme.LocalLevelColors

/**
 * Main measurement screen integrating all measurement modes
 * with seamless mode switching and error handling
 */
@Composable
fun MainMeasurementScreen(
    viewModel: LevelViewModel,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Start measurements when screen is first displayed
    LaunchedEffect(Unit) {
        if (uiState.sensorStatus == SensorStatus.INITIALIZING) {
            viewModel.handleIntent(LevelIntent.StartMeasurement)
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MainMeasurementTopBar(
                uiState = uiState,
                onSettingsClick = onSettingsClick,
                onRefreshClick = {
                    viewModel.handleIntent(LevelIntent.StartMeasurement)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mode selector
            ModeSelector(
                currentMode = uiState.measurementMode,
                onModeChange = { mode ->
                    viewModel.handleIntent(LevelIntent.SwitchMode(mode, savePreference = true))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Main measurement content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.error != null -> {
                        ErrorDisplay(
                            error = uiState.error!!,
                            onRetry = {
                                viewModel.handleIntent(LevelIntent.ClearError)
                                viewModel.handleIntent(LevelIntent.StartMeasurement)
                            },
                            onDismiss = {
                                viewModel.handleIntent(LevelIntent.ClearError)
                            }
                        )
                    }
                    
                    uiState.sensorStatus == SensorStatus.INITIALIZING -> {
                        LoadingDisplay()
                    }
                    
                    uiState.sensorStatus == SensorStatus.PERMISSION_DENIED -> {
                        PermissionDeniedDisplay(
                            onRequestPermission = {
                                viewModel.handleIntent(LevelIntent.RequestSensorPermission)
                            }
                        )
                    }
                    
                    uiState.sensorStatus == SensorStatus.UNAVAILABLE -> {
                        SensorUnavailableDisplay(
                            onRetry = {
                                viewModel.handleIntent(LevelIntent.StartMeasurement)
                            }
                        )
                    }
                    
                    else -> {
                        // Main measurement display with mode transition
                        ModeTransition(
                            currentMode = uiState.measurementMode,
                            content = { mode ->
                                MeasurementModeContent(
                                    mode = mode,
                                    uiState = uiState,
                                    onHoldToggle = {
                                        viewModel.handleIntent(LevelIntent.ToggleHold)
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top bar for the main measurement screen
 */
@Composable
private fun MainMeasurementTopBar(
    uiState: LevelUiState,
    onSettingsClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App title
            Text(
                text = "TrueLevel",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sensor status indicator
                SensorStatusIndicator(
                    status = uiState.sensorStatus,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                // Refresh button
                IconButton(onClick = onRefreshClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh sensors",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Settings button
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Sensor status indicator
 */
@Composable
private fun SensorStatusIndicator(
    status: SensorStatus,
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    val (color, text) = when (status) {
        SensorStatus.AVAILABLE -> levelColors.level to "Ready"
        SensorStatus.INITIALIZING -> levelColors.close to "Init"
        SensorStatus.UNAVAILABLE -> levelColors.notLevel to "N/A"
        SensorStatus.PERMISSION_DENIED -> levelColors.notLevel to "Perm"
        SensorStatus.ERROR -> levelColors.notLevel to "Error"
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Content for each measurement mode
 */
@Composable
private fun MeasurementModeContent(
    mode: MeasurementMode,
    uiState: LevelUiState,
    onHoldToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (mode) {
        MeasurementMode.DIGITAL_INCLINOMETER -> {
            DigitalInclinometerDisplay(
                measurement = uiState.displayMeasurement,
                isHeld = uiState.isHoldActive,
                modifier = modifier
            )
        }
        
        MeasurementMode.BUBBLE_LEVEL -> {
            BubbleLevelDisplay(
                measurement = uiState.displayMeasurement,
                isHeld = uiState.isHoldActive,
                modifier = modifier
            )
        }
        
        MeasurementMode.ANGLE_DISPLAY -> {
            // For now, use digital inclinometer with different styling
            // This can be expanded to a dedicated angle display component
            DigitalInclinometerDisplay(
                measurement = uiState.displayMeasurement,
                isHeld = uiState.isHoldActive,
                showSecondaryAngle = true,
                showAccuracy = true,
                modifier = modifier
            )
        }
    }
}

/**
 * Loading display while sensors are initializing
 */
@Composable
private fun LoadingDisplay(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Initializing sensors...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Error display with retry option
 */
@Composable
private fun ErrorDisplay(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = levelColors.notLevel.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = levelColors.notLevel,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineSmall,
                    color = levelColors.notLevel,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    
                    TextButton(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

/**
 * Permission denied display
 */
@Composable
private fun PermissionDeniedDisplay(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = levelColors.close.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sensor Permission Required",
                    style = MaterialTheme.typography.headlineSmall,
                    color = levelColors.close,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "TrueLevel needs access to your device's motion sensors to provide accurate level measurements.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onRequestPermission) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

/**
 * Sensor unavailable display
 */
@Composable
private fun SensorUnavailableDisplay(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = levelColors.notLevel.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sensors Unavailable",
                    style = MaterialTheme.typography.headlineSmall,
                    color = levelColors.notLevel,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "The required motion sensors are not available on this device or are currently in use by another application.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onRetry) {
                    Text("Try Again")
                }
            }
        }
    }
}