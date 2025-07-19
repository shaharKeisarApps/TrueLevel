package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable

/**
 * Sensor accuracy levels
 */
@Serializable
enum class SensorAccuracy {
    UNRELIABLE,
    LOW,
    MEDIUM,
    HIGH;
    
    /**
     * Gets the display name for this accuracy level
     */
    val displayName: String
        get() = when (this) {
            UNRELIABLE -> "Unreliable"
            LOW -> "Low"
            MEDIUM -> "Medium"
            HIGH -> "High"
        }
}