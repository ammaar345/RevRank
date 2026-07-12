package com.revrank.domain.model

import kotlin.math.roundToInt

/**
 * Domain model representing a trip.
 * Includes GPS points and G-force points for Pro features (route replay and G-force visualizer).
 */
data class Trip(
    val id: String,
    val userId: String,
    val vehicleType: VehicleType = VehicleType.CAR,
    val startTime: Long,
    val endTime: Long?,
    val startLat: Double = 0.0,
    val startLng: Double = 0.0,
    val endLat: Double = 0.0,
    val endLng: Double = 0.0,
    val distanceKm: Double,
    val durationSec: Long = 0,
    val maxSpeedKmh: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val routeName: String? = null,
    val score: Int,
    val scoreAcceleration: Int,
    val scoreBraking: Int,
    val scoreCornering: Int,
    val scoreSmoothness: Int,
    val scoreConsistency: Int,
    val shareImagePath: String?,
    val isSynced: Boolean = false,
    val wasShared: Boolean = false,
    // Pro features: route replay and G-force visualizer
    val gpsPoints: List<GpsPoint> = emptyList(),
    val gForcePoints: List<GForcePoint> = emptyList()
) {
    /** Compute a simple route hash based on start/end coordinates rounded to 2 decimal places (~1km accuracy). */
    fun computeRouteHash(): String {
        val startLatRounded = (startLat * 100).roundToInt()
        val startLngRounded = (startLng * 100).roundToInt()
        val endLatRounded = (endLat * 100).roundToInt()
        val endLngRounded = (endLng * 100).roundToInt()
        return "${startLatRounded}_${startLngRounded}_${endLatRounded}_${endLngRounded}"
    }
}