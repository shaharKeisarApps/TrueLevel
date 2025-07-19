package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable

/**
 * Sensor operational status
 */
@Serializable
enum class SensorStatus {
    AVAILABLE,
    UNAVAILABLE,
    PERMISSION_DENIED,
    INITIALIZING,
    ERROR
}