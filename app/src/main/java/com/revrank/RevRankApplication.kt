package com.revrank

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RevRankApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize RevenueCat
        // TODO: Replace "YOUR_REVENUECAT_API_KEY" with actual API key from secure config
        Purchases.configure(
            PurchasesConfiguration.Builder(this, "YOUR_REVENUECAT_API_KEY").build()
        )
    }
}
