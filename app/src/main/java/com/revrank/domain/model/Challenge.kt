package com.revrank.domain.model

import java.sql.Timestamp

/**
 * Represents a route challenge created by a user that others can accept.
 */
data class RouteChallenge(
    val id: String,
    val creatorUid: String,
    val creatorUsername: String,
    val creatorScore: Int,
    val routeHash: String,    // hash of start/end GPS coords ± 500m radius
    val tripId: String,       // the trip that the creator used for this challenge
    val createdAt: Timestamp,
    val expiresAt: Timestamp, // 7 days from creation
    val acceptances: List<ChallengeAcceptance> = emptyList()
)

/**
 * Represents a user's acceptance of a route challenge.
 */
data class ChallengeAcceptance(
    val uid: String,
    val username: String,
    val score: Int?,           // null until they drive the route and complete it
    val completedAt: Timestamp? // timestamp when they completed the challenge
)