package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable

/**
 * Available measurement modes
 */
@Serializable
enum class MeasurementMode {
    DIGITAL_INCLINOMETER,
    BUBBLE_LEVEL,
    ANGLE_DISPLAY
}