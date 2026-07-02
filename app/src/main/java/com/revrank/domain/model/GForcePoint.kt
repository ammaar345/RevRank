package com.revrank.domain.model

/**
 * Data class representing a G-force measurement at a point in time
 * Used for G-force visualizer in TripDetailScreen
 */
data class GForcePoint(
    val timestamp: Long,
    val lateralG: Float,
    val longitudinalG: Float,
    val scoreAtMoment: Int? = null
)