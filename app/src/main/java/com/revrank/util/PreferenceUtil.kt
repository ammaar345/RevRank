package com.revrank.util

import android.content.Context

/**
 * Simple wrapper around SharedPreferences for app settings.
 */
object PreferenceUtil {
    private const val PREFS_NAME = "revrank_prefs"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasSeenBatteryOptimizationPrompt(context: Context): Boolean =
        getPrefs(context).getBoolean("battery_optimization_prompt_shown", false)

    fun setHasSeenBatteryOptimizationPrompt(context: Context, value: Boolean) {
        getPrefs(context).edit().putBoolean("battery_optimization_prompt_shown", value).apply()
    }

    /** Tracks whether user has ever seen the paywall (for trial banner logic). */
    fun hasSeenPaywall(context: Context): Boolean =
        getPrefs(context).getBoolean("has_seen_paywall", false)

    fun setHasSeenPaywall(context: Context, value: Boolean) {
        getPrefs(context).edit().putBoolean("has_seen_paywall", value).apply()
    }

    /** Tracks whether the user has finished onboarding (survives process death). */
    fun hasCompletedOnboarding(context: Context): Boolean =
        getPrefs(context).getBoolean("onboarding_complete", false)

    fun setOnboardingComplete(context: Context, value: Boolean) {
        getPrefs(context).edit().putBoolean("onboarding_complete", value).apply()
    }
}