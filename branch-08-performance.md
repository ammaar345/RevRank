# Branch 08 — Performance & Reliability

## Prerequisites
Branch 07 merged. All features exist. This branch optimises GPS, battery, and background reliability.

---

## KalmanFilter.kt (replace stub from Branch 02)

Full implementation:
```kotlin
class LatLngKalmanFilter(
    private val processNoise: Double = 3.0  // metres/second uncertainty
) {
    private var lat = 0.0; private var lng = 0.0
    private var variance = -1.0  // negative = uninitialised
    
    fun process(newLat: Double, newLng: Double, accuracy: Float, timestampMs: Long): LatLng {
        val accuracyM = accuracy.toDouble()
        if (variance < 0) {
            lat = newLat; lng = newLng; variance = accuracyM * accuracyM
            lastTimestamp = timestampMs
            return LatLng(lat, lng)
        }
        val dt = (timestampMs - lastTimestamp) / 1000.0
        lastTimestamp = timestampMs
        variance += dt * processNoise * processNoise
        val k = variance / (variance + accuracyM * accuracyM)  // Kalman gain
        lat += k * (newLat - lat)
        lng += k * (newLng - lng)
        variance *= (1 - k)
        return LatLng(lat, lng)
    }
}
```

---

## Adaptive GPS Sampling

```kotlin
// In TripTrackingService:
private fun getLocationRequest(speedKmh: Float) = LocationRequest.Builder(
    Priority.PRIORITY_HIGH_ACCURACY,
    when {
        speedKmh < 5f  -> 30_000L  // stationary: every 30s
        speedKmh < 30f -> 5_000L   // slow: every 5s
        else           -> 1_000L   // moving: every 1s
    }
).apply {
    setMinUpdateDistanceMeters(if (speedKmh > 5f) 10f else 50f)
    setGranularity(Granularity.GRANULARITY_FINE)
}.build()

// Call updateLocationRequest(newSpeed) every 30s
```

---

## Polyline Compression — Ramer-Douglas-Peucker

```kotlin
// Before saving GPS points to Room (trips can have 3000+ raw points):
object RDPAlgorithm {
    fun simplify(points: List<LatLng>, epsilon: Double = 0.00005): List<LatLng> {
        if (points.size < 3) return points
        var maxDist = 0.0; var maxIdx = 0
        for (i in 1 until points.lastIndex) {
            val d = perpendicularDistance(points[i], points.first(), points.last())
            if (d > maxDist) { maxDist = d; maxIdx = i }
        }
        return if (maxDist > epsilon) {
            simplify(points.subList(0, maxIdx + 1), epsilon).dropLast(1) +
            simplify(points.subList(maxIdx, points.size), epsilon)
        } else listOf(points.first(), points.last())
    }
}
// Target: reduce to ~300 points per trip (sufficient for route replay)
```

---

## Background Service Survival

### Doze Mode
```kotlin
// In TripTrackingService.onStartCommand:
// Request partial wake lock to prevent CPU sleep during active tracking
val wakeLock = (getSystemService(POWER_SERVICE) as PowerManager)
    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TripRank::TrackingWakeLock")
wakeLock.acquire(2 * 60 * 60 * 1000L) // max 2 hours

// Release in onDestroy
wakeLock.release()
```

### WorkManager for AutoDetect restarts
```kotlin
// If service is killed, WorkManager restarts AutoTripDetector
// AutoTripDetector checks if a trip was in progress (Room: activeTripId != null)
// If yes: resume service, continue trip
// If no: wait for next movement detection
```

### Boot Receiver
```kotlin
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "auto_trip_detect",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<AutoTripDetector>(3, TimeUnit.MINUTES).build()
            )
        }
    }
}
```

---

## Sensor Batching

```kotlin
// Register with max report latency for non-critical sensors:
sensorManager.registerListener(
    this,
    accelerometer,
    SensorManager.SENSOR_DELAY_GAME,
    500_000  // 500ms max batch latency — reduces wake-ups
)
// Process batched events in onSensorChanged, accumulate into 5-second windows
```

---

## Offline-First Sync

```kotlin
// SyncManager.kt — runs when connectivity restored
class SyncManager @Inject constructor(
    private val tripDao: TripDao,
    private val firestore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager
) {
    fun scheduleSyncWhenConnected() {
        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .build()
        WorkManager.getInstance().enqueue(syncWork)
    }
}

class SyncWorker : CoroutineWorker(...) {
    override suspend fun doWork(): Result {
        val unsynced = tripDao.getUnsyncedTrips()
        unsynced.forEach { trip ->
            firestore.collection("trips").document(trip.id).set(trip.toFirestore())
            tripDao.markSynced(trip.id)
        }
        return Result.success()
    }
}
```

---

## GPS Cold Start Optimisation

```kotlin
// Use NETWORK_PROVIDER as first fix seed:
// 1. Get coarse location from network (fast, ~1 second)
// 2. Start GPS with this as seed (halves time to first fix)
// 3. Once GPS accuracy < 20m, switch exclusively to GPS

private var networkSeedObtained = false
locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, { location ->
    if (!networkSeedObtained) {
        lastKnownLocation = location
        networkSeedObtained = true
    }
}, null)
```

---

## Battery Impact Target: < 3% per hour

Profile with Android Studio Energy Profiler after each optimisation:
- GPS at 1Hz = ~1.5% per hour
- Accelerometer batched = ~0.3% per hour  
- Wake lock = ~0.5% per hour
- Total target = < 3%

Test on: Pixel 6a (baseline), Samsung Galaxy A54 (common OEM with aggressive battery kill).

---

## Acceptance Criteria
- [ ] Kalman filter eliminates GPS point jumps > 50m
- [ ] GPS sampling rate reduces correctly when stationary
- [ ] GPS polyline compressed to < 400 points per trip
- [ ] Service survives: screen off, app swipe, 30 min drive, Samsung battery saver
- [ ] Trips sync to Firestore when wifi reconnects after offline trip
- [ ] GPS first fix within 10 seconds (with network seed)
- [ ] Battery impact < 3% per hour measured in Energy Profiler
- [ ] No trip data lost when service is killed mid-trip
