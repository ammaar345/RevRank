package com.revrank.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.revrank.worker.AutoTripDetector
import java.util.concurrent.TimeUnit

/**
 * Receives ACTION_BOOT_COMPLETED and restarts the AutoTripDetector
 * periodic work so the app can detect vehicle movement without being opened.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "auto_trip_detect",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<AutoTripDetector>(
                    3, TimeUnit.MINUTES
                ).build()
            )
        }
    }
}
