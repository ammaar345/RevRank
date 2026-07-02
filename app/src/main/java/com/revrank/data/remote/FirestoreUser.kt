package com.revrank.data.remote

data class FirestoreUser(
    val uid: String = "",
    val username: String = "",
    val displayName: String = "",
    val rank: Int = 1,
    val xp: Int = 0,
    val totalTrips: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val streakDays: Int = 0,
    val lastTripDate: Long? = null,
    val isPro: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
