package com.revrank.domain.model

/**
 * Represents a single entry on the global or friends leaderboard.
 *
 * @param uid User ID
 * @param rank Global/Friends rank position (1‑based)
 * @param username Display name
 * @param avgScore Average score for the current week (0‑100)
 * @param totalTrips Number of trips taken this week
 * @param totalKm Total distance driven this week, in km
 * @param rankName Current rank name (e.g. "Driver", "Racer" …)
 * @param rankColorHex Hex colour of the current rank badge (e.g. "#00FF41")
 */
data class LeaderboardEntry(
    val uid: String,
    val rank: Int,
    val username: String,
    val avgScore: Int,
    val totalTrips: Int = 0,
    val totalKm: Double = 0.0,
    val rankName: String = "",
    val rankColorHex: String = "#00FF41"
)
