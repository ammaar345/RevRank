package com.revrank.domain.usecase

import com.revrank.domain.model.User

enum class PaywallTrigger {
    /** User tapped "Get Pro" / subscribe CTA. */
    EXPLICIT_UPGRADE,
    /** Triggered by ProUpsellBanner after dismissing a Pro feature gate. */
    PRO_FEATURE_GATE,
    /** App launch check (not used directly — passive ProGate handles it). */
    APP_LAUNCH
}

/**
 * Gate logic for when to show the paywall.
 * Rules:
 * - NEVER before user completes 3 trips (App Store guideline)
 * - NEVER if user is already Pro
 * - ALWAYS if user explicitly tapped an upgrade CTA (EXPLICIT_UPGRADE)
 * - ALWAYS for PRO_FEATURE_GATE triggers
 */
object PaywallTriggerManager {

    fun shouldShowPaywall(user: User, trigger: PaywallTrigger): Boolean {
        if (user.isPro) return false
        if (user.totalTrips < 3) return false  // App Store guideline
        return true
    }

    /** Simplified check when only trip count and Pro status are known. */
    fun shouldShowPaywall(isPro: Boolean, totalTrips: Int): Boolean {
        if (isPro) return false
        if (totalTrips < 3) return false
        return true
    }
}
