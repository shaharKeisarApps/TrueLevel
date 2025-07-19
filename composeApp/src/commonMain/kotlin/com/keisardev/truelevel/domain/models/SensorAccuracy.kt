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
    HIGH
}