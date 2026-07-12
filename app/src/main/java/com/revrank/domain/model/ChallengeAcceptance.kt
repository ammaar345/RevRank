package com.revrank.domain.model

/**
 * Represents a user's acceptance of a route challenge.
 * Defaults allow Firestore deserialization via the no-arg constructor.
 */
data class ChallengeAcceptance(
    val uid: String = "",
    val username: String = "",
    val score: Int? = null, // null until they complete a matching trip
    val completedAt: Long? = null // timestamp when they completed the matching trip
)