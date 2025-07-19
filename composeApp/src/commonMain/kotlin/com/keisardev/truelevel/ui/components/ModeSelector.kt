package com.keisardev.truelevel.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.keisardev.truelevel.domain.models.MeasurementMode
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Mode selector component with tabs and swipe gesture support
 */
@Composable
fun ModeSelector(
    currentMode: MeasurementMode,
    onModeChange: (MeasurementMode) -> Unit,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    enableSwipeGestures: Boolean = true
) {
    var dragOffset by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode tabs
        ModeTabs(
            currentMode = currentMode,
            onModeChange = onModeChange,
            showLabels = showLabels,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (enableSwipeGestures) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    val threshold = with(density) { 100.dp.toPx() }
                                    when {
                                        dragOffset > threshold -> {
                                            // Swipe right - previous mode
                                            val newMode = getPreviousMode(currentMode)
                                            onModeChange(newMode)
                                        }
                                        dragOffset < -threshold -> {
                                            // Swipe left - next mode
                                            val newMode = getNextMode(currentMode)
                                            onModeChange(newMode)
                                        }
                                    }
                                    dragOffset = 0f
                                }
                            ) { _, dragAmount ->
                                dragOffset += dragAmount
                            }
                        }
                    } else Modifier
                )
        )
        
        if (enableSwipeGestures) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Swipe indicator
            SwipeIndicator(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Mode tabs with animated selection indicator
 */
@Composable
private fun ModeTabs(
    currentMode: MeasurementMode,
    onModeChange: (MeasurementMode) -> Unit,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    val modes = MeasurementMode.values()
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            modes.forEach { mode ->
                ModeTab(
                    mode = mode,
                    isSelected = mode == currentMode,
                    onClick = { onModeChange(mode) },
                    showLabel = showLabels,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Individual mode tab with icon and optional label
 */
@Composable
private fun ModeTab(
    mode: MeasurementMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    showLabel: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.primary.copy(alpha = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getModeIcon(mode),
                contentDescription = getModeDisplayName(mode),
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            
            if (showLabel) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = getModeDisplayName(mode),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Swipe gesture indicator
 */
@Composable
private fun SwipeIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
            )
            
            if (index < 2) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

/**
 * Mode transition animation wrapper
 */
@Composable
fun ModeTransition(
    currentMode: MeasurementMode,
    content: @Composable (MeasurementMode) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = currentMode,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                // Sliding to next mode (left)
                slideInHorizontally { width -> width } + fadeIn(tween(300)) togetherWith 
                slideOutHorizontally { width -> -width } + fadeOut(tween(300))
            } else {
                // Sliding to previous mode (right)
                slideInHorizontally { width -> -width } + fadeIn(tween(300)) togetherWith
                slideOutHorizontally { width -> width } + fadeOut(tween(300))
            }
        },
        modifier = modifier
    ) { mode ->
        content(mode)
    }
}

/**
 * Get the icon for a measurement mode
 */
private fun getModeIcon(mode: MeasurementMode): ImageVector {
    return when (mode) {
        MeasurementMode.DIGITAL_INCLINOMETER -> Icons.Default.Straighten
        MeasurementMode.BUBBLE_LEVEL -> Icons.Default.WaterDrop
        MeasurementMode.ANGLE_DISPLAY -> Icons.Default.Architecture
    }
}

/**
 * Get the display name for a measurement mode
 */
private fun getModeDisplayName(mode: MeasurementMode): String {
    return when (mode) {
        MeasurementMode.DIGITAL_INCLINOMETER -> "Digital"
        MeasurementMode.BUBBLE_LEVEL -> "Bubble"
        MeasurementMode.ANGLE_DISPLAY -> "Angle"
    }
}

/**
 * Get the next mode in the sequence
 */
private fun getNextMode(currentMode: MeasurementMode): MeasurementMode {
    val modes = MeasurementMode.values()
    val currentIndex = modes.indexOf(currentMode)
    return modes[(currentIndex + 1) % modes.size]
}

/**
 * Get the previous mode in the sequence
 */
private fun getPreviousMode(currentMode: MeasurementMode): MeasurementMode {
    val modes = MeasurementMode.values()
    val currentIndex = modes.indexOf(currentMode)
    return modes[(currentIndex - 1 + modes.size) % modes.size]
}