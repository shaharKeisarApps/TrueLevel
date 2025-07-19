package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable

/**
 * Device orientation for calibration context
 */
@Serializable
enum class DeviceOrientation {
    PORTRAIT,
    LANDSCAPE_LEFT,
    LANDSCAPE_RIGHT,
    PORTRAIT_UPSIDE_DOWN
}