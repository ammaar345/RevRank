package com.revrank.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import com.revrank.service.TripTrackingService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically checks for vehicle activity to start/stop trip tracking automatically.
 * Runs every 3 minutes via WorkManager.
 * If the service was killed mid-trip, AutoTripDetector re-checks and restarts it.
 */
@HiltWorker
class AutoTripDetector @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val PREFS_NAME = "revrank_prefs"
        private const val KEY_IS_TRACKING = "is_tracking"
        private const val VEHICLE_CONFIDENCE_THRESHOLD = 75
    }

    override suspend fun doWork(): Result {
        return try {
            val activityClient = ActivityRecognition.getClient(applicationContext)
            val task = activityClient.detectActivity()
            val activityResult = com.google.android.gms.tasks.Tasks.await(task)

            val mostProbable = activityResult?.mostProbableActivity()
            val isInVehicle = mostProbable?.type == DetectedActivity.IN_VEHICLE &&
                    (mostProbable?.confidence ?: 0) >= VEHICLE_CONFIDENCE_THRESHOLD

            val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val wasTracking = prefs.getBoolean(KEY_IS_TRACKING, false)

            when {
                isInVehicle && !wasTracking -> {
                    val intent = android.content.Intent(
                        applicationContext,
                        TripTrackingService::class.java
                    ).apply { action = TripTrackingService.ACTION_START }
                    android.content.ContextCompat.startForegroundService(
                        applicationContext, intent
                    )
                    prefs.edit().putBoolean(KEY_IS_TRACKING, true).apply()
                }
                !isInVehicle && wasTracking -> {
                    val intent = android.content.Intent(
                        applicationContext,
                        TripTrackingService::class.java
                    ).apply { action = TripTrackingService.ACTION_STOP }
                    android.content.ContextCompat.startForegroundService(
                        applicationContext, intent
                    )
                    prefs.edit().putBoolean(KEY_IS_TRACKING, false).apply()
                }
            }

            // Re-enqueue for next check
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "auto_trip_detect",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<AutoTripDetector>(
                    3, TimeUnit.MINUTES
                ).build()
            )

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.success()
        }
    }
}
