package com.revrank.domain.model

/**
 * Driving quality classification based on acceleration/braking patterns.
 */
enum class DrivingQuality {
    SMOOTH,      // Gentle acceleration/braking
    MODERATE,    // Normal driving
    AGGRESSIVE   // Hard acceleration/braking, jerky
}