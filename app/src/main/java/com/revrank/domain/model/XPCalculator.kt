package com.revrank.domain.model

/**
 * Calculates XP earned from a trip, including streak and perfect-score bonuses.
 */
object XPCalculator {

    fun calculate(trip: Trip, streakBonus: Int): Int {
        val base = (trip.score * (trip.distanceKm / 10f).coerceAtLeast(1f)).toInt()
        val perfectBonus = if (trip.score == 100) 50 else 0
        val streakMultiplier = 1 + (streakBonus * 0.1f)
        return ((base + perfectBonus) * streakMultiplier).toInt()
    }
}
