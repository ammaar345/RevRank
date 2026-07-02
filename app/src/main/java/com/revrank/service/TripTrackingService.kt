package com.revrank.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.revrank.MainActivity
import com.revrank.R
import com.revrank.data.repository.TripTrackingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TripTrackingService : Service(), LocationListener, SensorEventListener {

    @Inject lateinit var trackingRepository: TripTrackingRepository

    companion object {
        const val ACTION_START = "com.revrank.ACTION_START"
        const val ACTION_STOP = "com.revrank.ACTION_STOP"
        const val ACTION_END_TRIP = "com.revrank.ACTION_END_TRIP"
        const val CHANNEL_ID = "trip_tracking_channel"
        const val NOTIF_ID = 1
        private const val TAG = "TripTracking"
        private const val STATIONARY_THRESHOLD_MS = 3 * 60 * 1000L
    }

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private var wakeLock: PowerManager.WakeLock? = null
    private var isTracking = false
    private var lastLocationTime: Long = 0
    private var lastLocation: Location? = null
    private var currentSpeedKmh: Float = 0f
    private var speedUpdateJob: Job? = null
    private var networkSeedObtained = false

    // Adaptive GPS intervals (milliseconds)
    private var gpsIntervalMs: Long = 5000L
    private var gpsMinDistanceM: Float = 10f

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when (it) {
                ACTION_START -> startTracking()
                ACTION_STOP -> stopTracking()
                ACTION_END_TRIP -> endTrip()
            }
        }
        return START_STICKY
    }

    private fun startTracking() {
        if (isTracking) return
        isTracking = true
        networkSeedObtained = false

        startForeground(NOTIF_ID, buildNotification())

        // Acquire partial wake lock (prevents CPU sleep during tracking)
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "RevRank::TrackingWakeLock"
        )
        wakeLock?.acquire(2 * 60 * 60 * 1000L) // max 2 hours

        // Step 1: Get coarse network seed for fast first fix
        seedFromNetwork()

        // Step 2: Start GPS with initial interval
        updateGpsRequest(gpsIntervalMs, gpsMinDistanceM)

        // Step 3: Register accelerometer with batching
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME,
                500_000  // 500ms max batch latency — reduces wake-ups
            )
        }

        // Step 4: Start speed-based interval adjustment loop
        speedUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            while (isTracking) {
                adjustGpsInterval()
                delay(30_000) // re-evaluate every 30s
            }
        }
    }

    /** Request coarse network location as seed for faster GPS fix. */
    private fun seedFromNetwork() {
        try {
            locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                { location ->
                    if (!networkSeedObtained && location != null) {
                        lastLocation = location
                        lastLocationTime = System.currentTimeMillis()
                        networkSeedObtained = true
                        Log.d(TAG, "Network seed obtained: ${location.latitude},${location.longitude}")
                    }
                },
                null
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "No network location permission", e)
        }
    }

    /** Updates GPS request with new interval and min distance. */
    private fun updateGpsRequest(intervalMs: Long, minDistanceM: Float) {
        try {
            locationManager.removeUpdates(this)
            val request = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                intervalMs
            ).apply {
                setMinUpdateDistanceMeters(minDistanceM)
                setGranularity(android.location.Granularity.GRANULARITY_FINE)
            }.build()

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                intervalMs,
                minDistanceM,
                this
            )
            gpsIntervalMs = intervalMs
            gpsMinDistanceM = minDistanceM
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission denied", e)
        } catch (e: Exception) {
            // Fallback for devices without Google Play Services
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                intervalMs,
                minDistanceM,
                this
            )
        }
    }

    /** Adjust GPS sampling rate based on current speed. */
    private fun adjustGpsInterval() {
        val interval: Long
        val minDist: Float
        when {
            currentSpeedKmh < 5f -> {
                interval = 30_000L  // stationary: every 30s
                minDist = 50f
            }
            currentSpeedKmh < 30f -> {
                interval = 5_000L   // slow: every 5s
                minDist = 10f
            }
            else -> {
                interval = 1_000L   // moving fast: every 1s
                minDist = 10f
            }
        }
        if (interval != gpsIntervalMs) {
            updateGpsRequest(interval, minDist)
            Log.d(TAG, "GPS interval adjusted: speed=${"%.1f".format(currentSpeedKmh)} km/h, interval=${interval}ms")
        }
    }

    private fun stopTracking() {
        isTracking = false
        speedUpdateJob?.cancel()
        speedUpdateJob = null
        stopForeground(true)
        try {
            locationManager.removeUpdates(this)
            sensorManager.unregisterListener(this)
        } catch (_: Exception) {}
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
        stopSelf()
    }

    private fun endTrip() {
        stopTracking()
        // Notify UI to navigate to trip end screen
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("END_TRIP", true)
        }
        PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onLocationChanged(location: Location) {
        // Filter bad accuracy readings
        if (location.accuracy > 25f) return

        // Update current speed for adaptive interval
        if (location.hasSpeed()) {
            currentSpeedKmh = location.speed * 3.6f
        }

        trackingRepository.onLocationChanged(
            location.latitude,
            location.longitude,
            location.accuracy
        )

        // Stationary detection
        val now = System.currentTimeMillis()
        if (lastLocation == null) {
            lastLocation = location
            lastLocationTime = now
        } else {
            val distance = location.distanceTo(lastLocation!!)
            if (distance >= 5f) {
                lastLocation = location
                lastLocationTime = now
            }
        }
        if (now - lastLocationTime > STATIONARY_THRESHOLD_MS) {
            endTrip()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Events are batched — process the accumulated values
            for (i in event.values.indices step 3) {
                if (i + 2 < event.values.size) {
                    trackingRepository.onAccelerometerChanged(
                        event.values[i],
                        event.values[i + 1],
                        event.values[i + 2]
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, TripTrackingService::class.java).apply {
            action = ACTION_END_TRIP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RevRank")
            .setContentText("● Trip — Recording")
            .setSmallIcon(R.drawable.ic_location)
            .addAction(R.drawable.ic_stop, "End Trip", stopPendingIntent)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Trip Tracking"
            val descriptionText = "Tracks your drives in background"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
