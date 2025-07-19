package com.keisardev.truelevel.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keisardev.truelevel.presentation.viewmodels.LevelViewModel
import com.keisardev.truelevel.ui.screens.MainMeasurementScreen

/**
 * Navigation routes for the TrueLevel app
 */
object LevelRoutes {
    const val MAIN_MEASUREMENT = "main_measurement"
    const val SETTINGS = "settings"
    const val CALIBRATION = "calibration"
    const val MEASUREMENT_HISTORY = "measurement_history"
}

/**
 * Main navigation component for the TrueLevel app
 */
@Composable
fun LevelNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: LevelViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LevelRoutes.MAIN_MEASUREMENT,
        modifier = modifier
    ) {
        composable(LevelRoutes.MAIN_MEASUREMENT) {
            MainMeasurementScreen(
                viewModel = viewModel,
                onSettingsClick = {
                    navController.navigate(LevelRoutes.SETTINGS)
                }
            )
        }
        
        composable(LevelRoutes.SETTINGS) {
            // Placeholder for settings screen - will be implemented in future tasks
            SettingsScreenPlaceholder(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(LevelRoutes.CALIBRATION) {
            // Placeholder for calibration screen - will be implemented in future tasks
            CalibrationScreenPlaceholder(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(LevelRoutes.MEASUREMENT_HISTORY) {
            // Placeholder for measurement history screen - will be implemented in future tasks
            MeasurementHistoryScreenPlaceholder(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Placeholder for settings screen
 */
@Composable
private fun SettingsScreenPlaceholder(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Coming Soon...",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBackClick) {
            Text("Back to Level")
        }
    }
}

/**
 * Placeholder for calibration screen
 */
@Composable
private fun CalibrationScreenPlaceholder(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Calibration Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Coming Soon...",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBackClick) {
            Text("Back to Level")
        }
    }
}

/**
 * Placeholder for measurement history screen
 */
@Composable
private fun MeasurementHistoryScreenPlaceholder(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Measurement History",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Coming Soon...",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBackClick) {
            Text("Back to Level")
        }
    }
}