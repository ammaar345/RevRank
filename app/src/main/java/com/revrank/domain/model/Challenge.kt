package com.revrank.domain.model

/**
 * Represents a route challenge created by a user that others can accept.
 * All fields have defaults so Firestore can deserialize via the no-arg constructor.
 * Timestamps are epoch millis.
 */
data class RouteChallenge(
    val id: String = "",
    val creatorUid: String = "",
    val creatorUsername: String = "",
    val creatorScore: Int = 0,
    val routeHash: String = "",   // hash of start/end GPS coords ± 500m radius
    val tripId: String = "",      // the trip that the creator used for this challenge
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L,     // 7 days from creation
    val acceptances: List<ChallengeAcceptance> = emptyList()
)
