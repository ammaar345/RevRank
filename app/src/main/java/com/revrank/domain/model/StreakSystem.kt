package com.revrank.domain.model

import java.time.LocalDate

/**
 * Pure function for streak calculation.
 * No side effects — called by UseCase or ViewModel.
 */
object StreakSystem {

    /**
     * Returns new streak days after considering today's trip.
     * @param lastTripDate most recent trip date (null if never)
     * @param currentStreak current consecutive streak
     * @param today today's date
     * @param hasFreeze whether user has a streak freeze available
     * @param isPro whether user has Pro (unlimited freezes)
     */
    fun updateStreak(
        lastTripDate: LocalDate?,
        currentStreak: Int,
        today: LocalDate,
        hasFreeze: Boolean,
        isPro: Boolean
    ): StreakResult {
        return when {
            // First ever trip
            lastTripDate == null -> StreakResult(1, false, 0)

            // Already drove today — no change
            lastTripDate == today -> StreakResult(currentStreak, false, 0)

            // Drove yesterday — extend streak
            lastTripDate == today.minusDays(1) -> StreakResult(currentStreak + 1, false, 0)

            // Missed one day — use freeze if available
            lastTripDate == today.minusDays(2) && (hasFreeze || isPro) -> {
                // Freeze consumed, streak continues from yesterday
                StreakResult(currentStreak, true, 1)
            }

            // Streak broken
            else -> StreakResult(1, false, 0)
        }
    }

    data class StreakResult(
        val newStreak: Int,
        val freezeConsumed: Boolean,
        val freezesUsed: Int
    )
}
