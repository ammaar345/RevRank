package com.revrank

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revrank.data.local.DemoData
import com.revrank.data.local.RevRankDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class RevRankApplication : Application() {

    // RevenueCat public SDK key. Replace via SETUP.md §4. A blank/placeholder key
    // must NOT crash the app on launch — the paywall simply shows no products.
    private val revenueCatApiKey = "YOUR_REVENUECAT_API_KEY"

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Firebase — safe to init even with a dummy google-services.json; online
        // calls just fail until a real config is supplied (see SETUP.md §2).
        runCatching { FirebaseApp.initializeApp(this) }
            .onFailure { Log.w(TAG, "Firebase init skipped: ${it.message}") }

        // RevenueCat — guard so a placeholder key can't take down launch.
        if (revenueCatApiKey.isNotBlank() && revenueCatApiKey != "YOUR_REVENUECAT_API_KEY") {
            runCatching {
                if (BuildConfig.DEBUG) Purchases.logLevel = LogLevel.DEBUG
                Purchases.configure(
                    PurchasesConfiguration.Builder(this, revenueCatApiKey).build()
                )
            }.onFailure { Log.w(TAG, "RevenueCat configure failed: ${it.message}") }
        } else {
            Log.i(TAG, "RevenueCat not configured (placeholder key) — paywall disabled.")
        }

        // Debug-only: seed sample trips so screens have content offline.
        if (BuildConfig.DEBUG) seedDemoDataIfEmpty()
    }

    private fun seedDemoDataIfEmpty() {
        appScope.launch {
            runCatching {
                val db = EntryPointAccessors
                    .fromApplication(this@RevRankApplication, AppDbEntryPoint::class.java)
                    .database()
                val dao = db.tripDao()
                val existing = dao.getUnsyncedTrips() // cheap existence probe
                // Only seed when there are no demo rows yet.
                if (dao.getTripById(DemoData.trips().first().id) == null && existing.isEmpty()) {
                    DemoData.trips().forEach { dao.insertTrip(it) }
                    Log.i(TAG, "Seeded ${DemoData.trips().size} demo trips.")
                }
            }.onFailure { Log.w(TAG, "Demo seed skipped: ${it.message}") }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppDbEntryPoint {
        fun database(): RevRankDatabase
    }

    companion object {
        private const val TAG = "RevRankApplication"
    }
}
