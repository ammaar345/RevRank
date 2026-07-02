package com.revrank.data.remote

data class FirestoreTrip(
    val id: String = "",
    val userId: String = "",
    val startTime: Long = 0L,
    val endTime: Long? = null,
    val distanceKm: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val score: Int = 0,
    val scoreAcceleration: Int = 0,
    val scoreBraking: Int = 0,
    val scoreCornering: Int = 0,
    val scoreSmoothness: Int = 0,
    val scoreConsistency: Int = 0,
    val routeName: String? = null,
    val shareImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
