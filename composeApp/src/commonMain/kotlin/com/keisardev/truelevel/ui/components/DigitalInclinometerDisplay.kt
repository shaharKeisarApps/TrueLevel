package com.keisardev.truelevel.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.keisardev.truelevel.domain.models.LevelMeasurement
import com.keisardev.truelevel.domain.models.LevelStatus
import com.keisardev.truelevel.ui.theme.LocalLevelColors
import com.keisardev.truelevel.ui.theme.getLevelStatusColor

/**
 * Digital inclinometer display component showing precise angle measurements
 * with color-coded level status indicators and hold functionality
 */
@Composable
fun DigitalInclinometerDisplay(
    measurement: LevelMeasurement?,
    isHeld: Boolean = false,
    modifier: Modifier = Modifier,
    showSecondaryAngle: Boolean = true,
    showAccuracy: Boolean = true
) {
    val levelColors = LocalLevelColors.current
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = levelColors.measurementBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hold indicator
            if (isHeld) {
                HoldIndicator(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Primary angle display
            PrimaryAngleDisplay(
                measurement = measurement,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Level status indicator
            LevelStatusIndicator(
                status = measurement?.levelStatus ?: LevelStatus.NOT_LEVEL,
                isHeld = isHeld,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Secondary information
            if (showSecondaryAngle && measurement != null) {
                SecondaryAngleDisplay(
                    measurement = measurement,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Accuracy indicator
            if (showAccuracy && measurement != null) {
                AccuracyIndicator(
                    accuracy = measurement.accuracy,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/**
 * Primary angle display with large, prominent text
 */
@Composable
private fun PrimaryAngleDisplay(
    measurement: LevelMeasurement?,
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    val statusColor = measurement?.let { getLevelStatusColor(it.levelStatus) } ?: Color.Gray
    
    AnimatedContent(
        targetState = measurement?.formatPrimaryAngle() ?: "--.-°",
        transitionSpec = {
            fadeIn(animationSpec = tween(150)) togetherWith 
            fadeOut(animationSpec = tween(150))
        },
        modifier = modifier
    ) { angleText ->
        Text(
            text = angleText,
            style = MaterialTheme.typography.displayLarge,
            color = statusColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Level status indicator with color-coded background and text
 */
@Composable
private fun LevelStatusIndicator(
    status: LevelStatus,
    isHeld: Boolean,
    modifier: Modifier = Modifier
) {
    val statusColor = getLevelStatusColor(status)
    val levelColors = LocalLevelColors.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Status indicator dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Status text
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = statusColor,
                fontWeight = FontWeight.Medium
            )
            
            // Hold indicator in status
            if (isHeld) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Measurement held",
                    tint = levelColors.holdIndicator,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Secondary angle display for Y-axis measurements
 */
@Composable
private fun SecondaryAngleDisplay(
    measurement: LevelMeasurement,
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // X-axis angle
        AngleAxisDisplay(
            label = "X",
            angle = measurement.formatPrimaryAngle(),
            color = getLevelStatusColor(LevelStatus.fromAngle(measurement.angleX))
        )
        
        // Y-axis angle
        AngleAxisDisplay(
            label = "Y",
            angle = measurement.formatSecondaryAngle(),
            color = getLevelStatusColor(LevelStatus.fromAngle(measurement.angleY))
        )
    }
}

/**
 * Individual axis angle display
 */
@Composable
private fun AngleAxisDisplay(
    label: String,
    angle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = angle,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Hold indicator showing measurement is frozen
 */
@Composable
private fun HoldIndicator(
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = levelColors.holdIndicator.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Hold active",
                tint = levelColors.holdIndicator,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "HOLD",
                style = MaterialTheme.typography.labelLarge,
                color = levelColors.holdIndicator,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Accuracy indicator showing sensor accuracy level
 */
@Composable
private fun AccuracyIndicator(
    accuracy: com.keisardev.truelevel.domain.models.SensorAccuracy,
    modifier: Modifier = Modifier
) {
    val accuracyColor = when (accuracy) {
        com.keisardev.truelevel.domain.models.SensorAccuracy.HIGH -> Color(0xFF4CAF50)
        com.keisardev.truelevel.domain.models.SensorAccuracy.MEDIUM -> Color(0xFFFF9800)
        com.keisardev.truelevel.domain.models.SensorAccuracy.LOW -> Color(0xFFF44336)
        com.keisardev.truelevel.domain.models.SensorAccuracy.UNRELIABLE -> Color(0xFF9E9E9E)
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Accuracy dots
        repeat(4) { index ->
            val isActive = when (accuracy) {
                com.keisardev.truelevel.domain.models.SensorAccuracy.HIGH -> index < 4
                com.keisardev.truelevel.domain.models.SensorAccuracy.MEDIUM -> index < 3
                com.keisardev.truelevel.domain.models.SensorAccuracy.LOW -> index < 2
                com.keisardev.truelevel.domain.models.SensorAccuracy.UNRELIABLE -> index < 1
            }
            
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) accuracyColor else accuracyColor.copy(alpha = 0.3f)
                    )
            )
            
            if (index < 3) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = accuracy.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}