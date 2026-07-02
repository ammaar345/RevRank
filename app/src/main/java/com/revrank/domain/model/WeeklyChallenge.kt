package com.revrank.domain.model

import java.time.LocalDate

/**
 * Domain model for a weekly challenge.
 */
data class WeeklyChallenge(
    val id: String,
    val type: ChallengeType,
    val title: String,
    val description: String,
    val xpReward: Int,
    val progress: Float,   // 0.0–1.0
    val completed: Boolean
)

sealed class ChallengeType {
    data class ScoreThreshold(val minScore: Int, val requiredTrips: Int) : ChallengeType()
    data class DistanceGoal(val targetKm: Float) : ChallengeType()
    data class CategoryScore(val category: ScoreCategory, val minScore: Int, val requiredTrips: Int) : ChallengeType()
    data class BeatPersonalBest(val currentPB: Int) : ChallengeType()
    data class NightTrips(val count: Int) : ChallengeType()
}

enum class ScoreCategory {
    ACCELERATION, BRAKING, CORNERING, SMOOTHNESS, CONSISTENCY
}
