package com.keisardev.truelevel.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.keisardev.truelevel.domain.models.LevelMeasurement
import com.keisardev.truelevel.domain.models.LevelStatus
import com.keisardev.truelevel.ui.theme.LocalLevelColors
import com.keisardev.truelevel.ui.theme.getLevelStatusColor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Bubble level display component with realistic vial interface
 * Simulates traditional bubble level with smooth bubble movement
 */
@Composable
fun BubbleLevelDisplay(
    measurement: LevelMeasurement?,
    isHeld: Boolean = false,
    modifier: Modifier = Modifier,
    vialWidth: Dp = 300.dp,
    vialHeight: Dp = 80.dp,
    showAngles: Boolean = true
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
            
            // Main bubble level vial
            BubbleLevelVial(
                measurement = measurement,
                vialWidth = vialWidth,
                vialHeight = vialHeight,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Level status indicator
            LevelStatusIndicator(
                status = measurement?.levelStatus ?: LevelStatus.NOT_LEVEL,
                isHeld = isHeld,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Angle readings
            if (showAngles && measurement != null) {
                AngleReadings(
                    measurement = measurement,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

/**
 * Main bubble level vial component with animated bubble
 */
@Composable
private fun BubbleLevelVial(
    measurement: LevelMeasurement?,
    vialWidth: Dp,
    vialHeight: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(vialWidth, vialHeight)
            .shadow(8.dp, RoundedCornerShape(vialHeight / 2))
    ) {
        // Vial background
        LevelVial(
            width = vialWidth,
            height = vialHeight,
            modifier = Modifier.fillMaxSize()
        )
        
        // Level markers
        LevelMarkers(
            width = vialWidth,
            height = vialHeight,
            modifier = Modifier.fillMaxSize()
        )
        
        // Animated bubble
        AnimatedBubble(
            angleX = measurement?.angleX ?: 0.0,
            angleY = measurement?.angleY ?: 0.0,
            vialWidth = vialWidth,
            vialHeight = vialHeight,
            levelStatus = measurement?.levelStatus ?: LevelStatus.NOT_LEVEL,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Level vial background with traditional styling
 */
@Composable
private fun LevelVial(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Canvas(modifier = modifier) {
        val vialWidth = with(density) { width.toPx() }
        val vialHeight = with(density) { height.toPx() }
        val cornerRadius = vialHeight / 2f
        
        // Outer vial (metal frame)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFBDBDBD),
                    Color(0xFF757575),
                    Color(0xFFBDBDBD)
                ),
                start = Offset(0f, 0f),
                end = Offset(0f, vialHeight)
            ),
            size = Size(vialWidth, vialHeight),
            cornerRadius = CornerRadius(cornerRadius)
        )
        
        // Inner vial (liquid chamber)
        val innerPadding = 6f
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF5F5F5),
                    Color(0xFFE0E0E0),
                    Color(0xFFBDBDBD)
                ),
                center = Offset(vialWidth / 2f, vialHeight / 2f),
                radius = vialHeight / 2f
            ),
            topLeft = Offset(innerPadding, innerPadding),
            size = Size(vialWidth - innerPadding * 2, vialHeight - innerPadding * 2),
            cornerRadius = CornerRadius(cornerRadius - innerPadding)
        )
        
        // Liquid (greenish tint)
        drawRoundRect(
            color = Color(0xFFE8F5E8),
            topLeft = Offset(innerPadding * 1.5f, innerPadding * 1.5f),
            size = Size(vialWidth - innerPadding * 3, vialHeight - innerPadding * 3),
            cornerRadius = CornerRadius(cornerRadius - innerPadding * 1.5f)
        )
    }
}

/**
 * Level markers and center zone indicators
 */
@Composable
private fun LevelMarkers(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Canvas(modifier = modifier) {
        val vialWidth = with(density) { width.toPx() }
        val vialHeight = with(density) { height.toPx() }
        val centerX = vialWidth / 2f
        val centerY = vialHeight / 2f
        
        // Center zone (level area)
        val centerZoneWidth = vialWidth * 0.15f
        drawRoundRect(
            color = Color(0x40000000),
            topLeft = Offset(centerX - centerZoneWidth / 2f, centerY - vialHeight * 0.3f),
            size = Size(centerZoneWidth, vialHeight * 0.6f),
            cornerRadius = CornerRadius(4f)
        )
        
        // Center line
        drawLine(
            color = Color(0x80000000),
            start = Offset(centerX, centerY - vialHeight * 0.4f),
            end = Offset(centerX, centerY + vialHeight * 0.4f),
            strokeWidth = 2f
        )
        
        // Degree markers
        val markerSpacing = vialWidth / 20f
        for (i in -8..8) {
            if (i == 0) continue // Skip center line
            
            val x = centerX + i * markerSpacing
            val isMainMarker = i % 2 == 0
            val markerHeight = if (isMainMarker) vialHeight * 0.2f else vialHeight * 0.1f
            
            drawLine(
                color = Color(0x60000000),
                start = Offset(x, centerY - markerHeight / 2f),
                end = Offset(x, centerY + markerHeight / 2f),
                strokeWidth = if (isMainMarker) 2f else 1f
            )
        }
    }
}

/**
 * Animated bubble component with smooth movement
 */
@Composable
private fun AnimatedBubble(
    angleX: Double,
    angleY: Double,
    vialWidth: Dp,
    vialHeight: Dp,
    levelStatus: LevelStatus,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // Calculate bubble position based on angles
    val maxOffset = with(density) { (vialWidth - 60.dp).toPx() / 2f }
    val targetOffsetX = (angleX * 10.0).coerceIn(-maxOffset.toDouble(), maxOffset.toDouble())
    
    // Animate bubble position
    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffsetX.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Bubble
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(animatedOffsetX.roundToInt(), 0)
                }
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = getBubbleColors(levelStatus),
                        center = Offset(0.3f, 0.3f)
                    )
                )
        )
    }
}

/**
 * Get bubble colors based on level status
 */
private fun getBubbleColors(levelStatus: LevelStatus): List<Color> {
    return when (levelStatus) {
        LevelStatus.LEVEL -> listOf(
            Color(0xFF81C784),
            Color(0xFF4CAF50),
            Color(0xFF2E7D32)
        )
        LevelStatus.CLOSE -> listOf(
            Color(0xFFFFB74D),
            Color(0xFFFF9800),
            Color(0xFFE65100)
        )
        LevelStatus.NOT_LEVEL -> listOf(
            Color(0xFFE57373),
            Color(0xFFF44336),
            Color(0xFFC62828)
        )
    }
}

/**
 * Level status indicator for bubble level
 */
@Composable
private fun LevelStatusIndicator(
    status: LevelStatus,
    isHeld: Boolean,
    modifier: Modifier = Modifier
) {
    val statusColor = getLevelStatusColor(status)
    val levelColors = LocalLevelColors.current
    
    Surface(
        modifier = modifier,
        color = statusColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Status indicator circle
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
            
            // Hold indicator
            if (isHeld) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HELD",
                    style = MaterialTheme.typography.labelMedium,
                    color = levelColors.holdIndicator,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Angle readings display
 */
@Composable
private fun AngleReadings(
    measurement: LevelMeasurement,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // X-axis angle
        AngleReading(
            label = "X-Axis",
            angle = measurement.formatPrimaryAngle(),
            color = getLevelStatusColor(LevelStatus.fromAngle(measurement.angleX))
        )
        
        // Y-axis angle
        AngleReading(
            label = "Y-Axis",
            angle = measurement.formatSecondaryAngle(),
            color = getLevelStatusColor(LevelStatus.fromAngle(measurement.angleY))
        )
    }
}

/**
 * Individual angle reading display
 */
@Composable
private fun AngleReading(
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
 * Hold indicator for bubble level
 */
@Composable
private fun HoldIndicator(
    modifier: Modifier = Modifier
) {
    val levelColors = LocalLevelColors.current
    
    Surface(
        modifier = modifier,
        color = levelColors.holdIndicator.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "MEASUREMENT HELD",
            style = MaterialTheme.typography.labelLarge,
            color = levelColors.holdIndicator,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}