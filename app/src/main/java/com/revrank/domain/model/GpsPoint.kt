package com.revrank.domain.model

/**
 * Data class representing a GPS point with optional score for route replay coloring.
 */
data class GpsPoint(
    val lat: Double,
    val lng: Double,
    val timestamp: Long,
    val speedKmh: Float?,
    val accuracy: Float?,
    val score: Int? = null // Score at this point (for coloring polyline segments)
)