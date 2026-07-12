package com.revrank.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.revrank.domain.model.GForcePoint
import com.revrank.domain.model.GpsPoint
import com.revrank.domain.model.DrivingQuality
import com.revrank.utils.LatLngKalmanFilter
import com.revrank.utils.TripScoreCalculator
import com.revrank.utils.TripScoreCalculator.SensorWindow
import com.revrank.utils.TripScoreCalculator.ScoreBreakdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that holds the live state of an ongoing trip.
 * Updated by TripTrackingService and collected by UI.
 * Also buffers GPS and G-force points for later persistence (Pro features).
 */
@Singleton
class TripTrackingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _currentSpeed = MutableStateFlow(0.0)
    val currentSpeed: StateFlow<Double> = _currentSpeed.asStateFlow()

    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _drivingQuality = MutableStateFlow(DrivingQuality.SMOOTH)
    val drivingQuality: StateFlow<DrivingQuality> = _drivingQuality.asStateFlow()

    private val _scoreBreakdown = MutableStateFlow(ScoreBreakdown(0, 0, 0, 0, 0))
    val scoreBreakdown: StateFlow<ScoreBreakdown> = _scoreBreakdown.asStateFlow()

    // Internal processing
    private val kalmanFilter = LatLngKalmanFilter(processNoise = 3.0)
    private val scoreCalculator = TripScoreCalculator()

    // Buffers for sensor windows
    private val accelBuffer = mutableListOf<Float>()
    private val lateralBuffer = mutableListOf<Float>()
    private val speedBuffer = mutableListOf<Float>()

    // Buffers for Pro features: GPS points and G-force points
    private val gpsPointsBuffer = mutableListOf<GpsPoint>()
    private val gForcePointsBuffer = mutableListOf<GForcePoint>()

    private var lastLat: Double = 0.0
    private var lastLng: Double = 0.0
    private var lastTimeMillis: Long = 0
    private var totalDistanceMeters: Double = 0.0
    private var totalElapsedSeconds: Double = 0.0

    fun startTrip(initialLat: Double, initialLng: Double) {
        lastLat = initialLat
        lastLng = initialLng
        lastTimeMillis = System.currentTimeMillis()
        totalDistanceMeters = 0.0
        totalElapsedSeconds = 0.0

        // Clear all buffers
        accelBuffer.clear()
        lateralBuffer.clear()
        speedBuffer.clear()
        gpsPointsBuffer.clear()
        gForcePointsBuffer.clear()

        // Reset Kalman filter
        kalmanFilter.reset(initialLat, initialLng)

        // Reset counters
        _elapsedSeconds.value = 0
        _distanceKm.value = 0.0
        _currentSpeed.value = 0.0
        _currentScore.value = 0
        _drivingQuality.value = DrivingQuality.SMOOTH
        _scoreBreakdown.value = ScoreBreakdown(0, 0, 0, 0, 0)
    }

    fun stopTrip() {
        // Optionally reset or keep last values
    }

    /** Call from location callback */
    fun onLocationChanged(latitude: Double, longitude: Double, accuracy: Float) {
        val now = System.currentTimeMillis()
        // Filter the raw input through Kalman
        val (filteredLat, filteredLng) = kalmanFilter.process(
            latitude, longitude, accuracy, now
        )

        if (lastTimeMillis > 0) {
            val dt = (now - lastTimeMillis) / 1000.0   // seconds
            if (dt > 0) {
                // Update elapsed time and distance
                totalElapsedSeconds += dt
                val distance = haversineDistance(lastLat, lastLng, filteredLat, filteredLng)
                totalDistanceMeters += distance
                // Instantaneous speed in m/s then convert to km/h
                val speedMs = distance / dt
                val speedKmh = speedMs * 3.6
                _currentSpeed.value = speedKmh
                // Update distance in km
                _distanceKm.value = totalDistanceMeters / 1000.0
                // Update elapsed seconds
                _elapsedSeconds.value = totalElapsedSeconds.toLong()
            }
        } else {
            // First point: set speed to 0
            _currentSpeed.value = 0.0
            _distanceKm.value = 0.0
            _elapsedSeconds.value = 0
        }

        // Store GPS point for Pro features (route replay)
        val gpsPoint = GpsPoint(
            lat = filteredLat,
            lng = filteredLng,
            timestamp = now,
            speedKmh = _currentSpeed.value.toFloat(),
            accuracy = accuracy
        )
        gpsPointsBuffer.add(gpsPoint)
        
        // Limit buffer size to prevent memory issues (keep last 2000 points)
        if (gpsPointsBuffer.size > 2000) {
            gpsPointsBuffer.removeAt(0)
        }

        lastLat = filteredLat
        lastLng = filteredLng
        lastTimeMillis = now
    }

    /** Call from accelerometer callback */
    fun onAccelerometerChanged(accelX: Float, accelY: Float, accelZ: Float) {
        // Assuming device mounted in car: X = forward, Y = lateral, Z = vertical
        accelBuffer.add(accelX)
        lateralBuffer.add(accelY)
        speedBuffer.add(_currentSpeed.value.toFloat())

        if (accelBuffer.size > 25) {
            accelBuffer.removeAt(0)
            lateralBuffer.removeAt(0)
            speedBuffer.removeAt(0)
        }

        // When we have enough samples, compute score
        if (accelBuffer.size >= 20) {
            val window = SensorWindow(
                accelerations = ArrayList(accelBuffer),
                laterals = ArrayList(lateralBuffer),
                speeds = ArrayList(speedBuffer)
            )
            val result = scoreCalculator.calculateScore(window)
            _currentScore.value = result.total
            _drivingQuality.value = when {
                result.total >= 80 -> DrivingQuality.SMOOTH
                result.total >= 50 -> DrivingQuality.MODERATE
                else -> DrivingQuality.AGGRESSIVE
            }
            _scoreBreakdown.value = result  // Update the breakdown
            
            // Store G-force point for Pro features (G-force visualizer)
            val gForcePoint = GForcePoint(
                timestamp = System.currentTimeMillis(),
                lateralG = accelY,
                longitudinalG = accelX,
                scoreAtMoment = result.total
            )
            gForcePointsBuffer.add(gForcePoint)
            
            // Limit buffer size to prevent memory issues (keep last 1000 points)
            if (gForcePointsBuffer.size > 1000) {
                gForcePointsBuffer.removeAt(0)
            }
            
            // Clear buffers after processing to avoid overlap
            accelBuffer.clear()
            lateralBuffer.clear()
            speedBuffer.clear()
        }
    }

    /** Get the collected GPS points for route replay */
    fun getGpsPoints(): List<GpsPoint> {
        return gpsPointsBuffer.toList()
    }

    /** Get the collected G-force points for G-force visualizer */
    fun getGForcePoints(): List<GForcePoint> {
        return gForcePointsBuffer.toList()
    }

    /** Clear the point buffers (called after trip is ended and points are persisted) */
    fun clearPointBuffers() {
        gpsPointsBuffer.clear()
        gForcePointsBuffer.clear()
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
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