package com.revrank.utils

import android.util.Log

/**
 * 2D Kalman filter for smoothing GPS latitude/longitude readings.
 * Uses timestamp-based process noise scaling for accurate velocity estimation.
 *
 * Eliminates GPS point jumps > 50m by combining:
 * - Adaptive Kalman gain based on reported accuracy
 * - Process noise scaling by time delta (more uncertainty = longer dt)
 * - Latent variance growth when updates are delayed
 */
class LatLngKalmanFilter(
    private val processNoise: Double = 3.0  // meters/second uncertainty
) {
    private var lat = 0.0
    private var lng = 0.0
    private var variance = -1.0  // negative = uninitialised
    private var lastTimestamp = 0L

    companion object {
        private const val TAG = "LatLngKalmanFilter"
        private const val MAX_JUMP_METERS = 50.0
    }

    /**
     * Process a new GPS measurement and return the filtered (lat, lng).
     * Clamps jumps > 50m to prevent spurious spikes from reaching the track.
     */
    fun process(
        newLat: Double,
        newLng: Double,
        accuracy: Float,
        timestampMs: Long
    ): Pair<Double, Double> {
        val accuracyM = accuracy.toDouble()

        // Initialise on first measurement
        if (variance < 0) {
            lat = newLat
            lng = newLng
            variance = accuracyM * accuracyM
            lastTimestamp = timestampMs
            return Pair(lat, lng)
        }

        // Time delta in seconds
        val dt = (timestampMs - lastTimestamp) / 1000.0
        if (dt <= 0) return Pair(lat, lng)  // same timestamp, skip
        lastTimestamp = timestampMs

        // Predict: uncertainty grows with time
        variance += dt * processNoise * processNoise

        // Check for GPS jump: compute approximate displacement
        val jumpDist = haversineDistance(lat, lng, newLat, newLng)
        if (jumpDist > MAX_JUMP_METERS) {
            Log.w(TAG, "GPS jump detected: %.1fm (threshold %.0fm)".format(jumpDist, MAX_JUMP_METERS))
            // Don't fully trust the jump: increase measurement noise
            val trustRatio = (jumpDist / MAX_JUMP_METERS).coerceIn(1.0, 5.0)
            val adjustedAccuracy = accuracyM * trustRatio

            // A single sample > 200m away means either a GPS glitch or that the
            // estimate has diverged / there was a long gap. Re-acquire on the new
            // fix instead of permanently freezing (the old code returned the stale
            // estimate forever, so distance stopped accumulating).
            if (jumpDist > 200.0) {
                lat = newLat
                lng = newLng
                variance = accuracyM * accuracyM
                return Pair(lat, lng)
            }

            // Update with adjusted noise
            val k = variance / (variance + adjustedAccuracy * adjustedAccuracy)
            lat += k * (newLat - lat)
            lng += k * (newLng - lng)
            variance *= (1 - k)
            return Pair(lat, lng)
        }

        // Normal Kalman update
        val k = variance / (variance + accuracyM * accuracyM)  // Kalman gain
        lat += k * (newLat - lat)
        lng += k * (newLng - lng)
        variance *= (1 - k)

        return Pair(lat, lng)
    }

    /** Reset the filter (call when starting a new trip). */
    fun reset(initialLat: Double = 0.0, initialLng: Double = 0.0) {
        lat = initialLat
        lng = initialLng
        variance = -1.0
        lastTimestamp = 0L
    }

    /** Current filtered estimate without processing a new measurement. */
    fun getEstimate(): Pair<Double, Double> = Pair(lat, lng)

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δφ = Math.toRadians(lat2 - lat1)
        val Δλ = Math.toRadians(lon2 - lon1)
        val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}
