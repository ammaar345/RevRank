package com.revrank.util

import android.content.Context
import androidx.preference.PreferenceManager

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
    var hasSeenPaywall: Boolean
        get() = getPrefs(android.app.Application.getProcessApplicationContext())
            .getBoolean("has_seen_paywall", false)
        set(value) {
            getPrefs(android.app.Application.getProcessApplicationContext())
                .edit().putBoolean("has_seen_paywall", value).apply()
        }
}