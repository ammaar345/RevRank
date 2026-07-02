# Branch 02 — Trip Tracking

## Prerequisites
Branch 01 merged. Auth, user profile, Room DB exist.

## Architecture

### TripTrackingService.kt (Foreground Service)
```kotlin
class TripTrackingService : Service() {
    // Foreground service — MUST show persistent notification
    // Notification: "● TripRank — Recording" with [End Trip] action
    // Survives Doze, screen off, app backgrounded
    
    // Components:
    // - FusedLocationProviderClient (5s interval while moving, 30s while idle)
    // - SensorManager (accelerometer + gyroscope at SENSOR_DELAY_GAME)
    // - TripScoreCalculator (runs on sensor data stream)
    // - KalmanFilter (applied to GPS points)
    
    // StateFlow exposed to UI:
    val currentSpeed: StateFlow<Float>
    val currentScore: StateFlow<Int>
    val distanceKm: StateFlow<Float>
    val elapsedSeconds: StateFlow<Long>
    val drivingQuality: StateFlow<DrivingQuality> // SMOOTH, MODERATE, AGGRESSIVE
}
```

### AutoTripDetector.kt (WorkManager PeriodicWork)
```kotlin
// Runs every 3 minutes in background
// Uses ActivityRecognitionClient to detect IN_VEHICLE
// When confidence >70%: starts TripTrackingService
// When stationary 3+ minutes: sends STOP intent to service
class AutoTripDetector : CoroutineWorker(...)
```

### KalmanFilter.kt
Simple 1D Kalman filter applied per GPS coordinate:
```kotlin
class KalmanFilter(private val processNoise: Float = 0.01f) {
    private var estimate = 0.0
    private var errorEstimate = 1.0
    fun filter(measurement: Double, measurementNoise: Float): Double
}
// Apply independently to lat and lng
// Also filter speed: eliminates GPS speed spikes
```

### TripScoreCalculator.kt
```kotlin
class TripScoreCalculator {
    // Maintains rolling window of sensor events
    // Emits updated score every 5 seconds
    
    data class SensorWindow(
        val accelerations: List<Float>,   // longitudinal g-force
        val laterals: List<Float>,         // lateral g-force  
        val speeds: List<Float>
    )
    
    fun calculateScore(window: SensorWindow): ScoreBreakdown {
        // acceleration score: penalise events > 0.35g
        // braking score: penalise events > 0.4g deceleration
        // cornering score: penalise lateral > 0.4g
        // smoothness: std deviation of speed
        // consistency: variance from mean speed
        // final = weighted average, clamped 0–100
    }
    
    data class ScoreBreakdown(
        val total: Int,
        val acceleration: Int,
        val braking: Int,
        val cornering: Int,
        val smoothness: Int,
        val consistency: Int,
        val hardBrakingCount: Int,
        val hardAccelCount: Int,
        val hardCorneringCount: Int
    )
}
```

### TripRepository additions
```kotlin
suspend fun startTrip(userId: String): Trip
suspend fun endTrip(tripId: String, finalScore: ScoreBreakdown, gpsPoints: List<LatLng>): Trip
suspend fun updateLiveScore(tripId: String, score: Int)
fun getActiveTripFlow(): Flow<Trip?>
```

## LiveHudScreen.kt

Full-screen composable. No bottom navigation during trip.

Layout:
```
- StatusBar: transparent, edge-to-edge
- TOP: small pill — red dot + "RECORDING" + elapsed time (Share Tech Mono, 14sp)
- CENTER: speed number (96sp Share Tech Mono) + "KM/H" label (12sp, letter-spacing 4sp)
- BORDER GLOW: entire screen has colored glow based on DrivingQuality
    SMOOTH    = MatrixGreen glow (rgba 0,255,65, 0.2)
    MODERATE  = Amber glow (rgba 255,140,0, 0.2)
    AGGRESSIVE = Red glow (rgba 255,45,0, 0.3)
- BOTTOM STATS ROW: distance | live score (two equal columns, Share Tech Mono)
- FAB: "■ END TRIP" — large, full-width, bottom, 64dp height
```

Border glow implementation: `Box` with `Modifier.border(width=2.dp, brush=glowBrush, shape=RectangleShape)` + `Modifier.shadow(elevation=16.dp, ambientColor=glowColor)`

Speed needle animation: animate speed value with `animateFloatAsState(targetValue=speed, animationSpec=spring(stiffness=150f, dampingRatio=0.8f))` — gives analog lag feel.

### Screen States
- `IDLE` — "Tap to start a trip manually" CTA + "Auto-detect is on" label
- `TRACKING` — full HUD
- `ENDING` — brief "Calculating score..." spinner before navigating to TripEndScreen

## Permissions Required in Manifest
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION"/>
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION"/>
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<!-- Restart service on boot -->
```

## Battery Optimisation Prompt
On first trip start, check if app is excluded from battery optimisation.
If not: show dialog explaining why → open `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.

## GPS Accuracy Thresholds
- Only record GPS point if `accuracy < 25 metres`
- If 3+ consecutive low-accuracy points: show signal warning on HUD
- Minimum trip distance to save: 500m (discard shorter trips silently)

## Acceptance Criteria
- [ ] Auto-detection starts trip within 45 seconds of driving
- [ ] Service survives screen off and app swipe-away
- [ ] Kalman filter visibly smooths GPS on replay
- [ ] Score updates live every 5 seconds on HUD
- [ ] Border glow changes colour based on driving quality
- [ ] Trip under 500m not saved
- [ ] Battery optimisation prompt shown on first trip
