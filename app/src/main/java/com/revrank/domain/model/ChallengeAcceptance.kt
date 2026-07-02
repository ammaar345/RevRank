package com.revrank.domain.model

import java.util.Date

/**
 * Represents a user's acceptance of a route challenge.
 */
data class ChallengeAcceptance(
    val uid: String,
    val username: String,
    val score: Int? = null, // null until they complete a matching trip
    val completedAt: Long? = null // timestamp when they completed the matching trip
)