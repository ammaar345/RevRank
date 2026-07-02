package com.revrank.domain.model

data class User(
    val uid: String,
    val username: String,
    val displayName: String,
    val rank: Int = 1,
    val xp: Int = 0,
    val totalTrips: Int = 0,
    val totalDistanceKm: Float = 0f,
    val streakDays: Int = 0,
    val lastTripDate: Long?,
    val isPro: Boolean = false,
    val pendingChallengeIds: List<String> = emptyList(),
    val referredBy: String? = null,
    val hasUsedReferral: Boolean = false
)
