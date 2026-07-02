package com.revrank.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.revrank.data.local.dao.TripDao
import com.revrank.data.remote.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager worker that syncs unsynced trips to Firestore when connectivity is restored.
 * Scheduled by SyncManager when network comes back after an offline trip.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncManager.syncUnsyncedTrips()
            if (applicationContext.getSharedPreferences("revrank_prefs", Context.MODE_PRIVATE)
                    .getBoolean("is_tracking", false)
            ) {
                // If a trip was in progress when killed, re-enqueue periodic detection
                Result.retry()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
