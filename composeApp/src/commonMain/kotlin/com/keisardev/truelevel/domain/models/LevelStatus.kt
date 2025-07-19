package com.keisardev.truelevel.domain.models

import kotlinx.serialization.Serializable

/**
 * Level status enumeration based on angle thresholds
 */
@Serializable
enum class LevelStatus {
    LEVEL,      // Within ±1 degree
    CLOSE,      // 1-5 degrees from level
    NOT_LEVEL   // >5 degrees from level
}