package com.revrank.domain.usecase

import com.revrank.domain.model.ChallengeType
import com.revrank.domain.model.ScoreCategory
import com.revrank.domain.model.Trip
import com.revrank.domain.model.WeeklyChallenge
import java.util.UUID
import javax.inject.Inject

/**
 * Generates 3 weekly challenges personalised to the user's weak areas.
 * Called every Monday by WorkManager or Cloud Function.
 */
class WeeklyChallengeGenerator @Inject constructor() {

    fun generate(recentTrips: List<Trip>): List<WeeklyChallenge> {
        val weakest = findWeakestCategory(recentTrips) ?: ScoreCategory.BRAKING

        // Challenge 1: Target weakest category
        val c1 = WeeklyChallenge(
            id = UUID.randomUUID().toString(),
            type = ChallengeType.CategoryScore(weakest, 80, 3),
            title = "${weakest.name.lowercase().replaceFirstChar { it.uppercase() }} Master",
            description = "Score 80+ in ${weakest.name.lowercase()} on 3 trips",
            xpReward = 100,
            progress = 0f,
            completed = false
        )

        // Challenge 2: Distance goal scaled to user's average.
        // Guard against an empty trip list (average() of empty = NaN → "Drive NaN km").
        val distances = recentTrips.filter { it.endTime != null }.map { it.distanceKm }
        val avgDist = if (distances.isEmpty()) 0.0 else distances.average()
        val targetKm = (avgDist * 1.2 + 10.0).coerceAtLeast(25.0).toFloat() // Minimum 25km
        val c2 = WeeklyChallenge(
            id = UUID.randomUUID().toString(),
            type = ChallengeType.DistanceGoal(targetKm),
            title = "Road Warrior",
            description = "Drive %.0f km this week".format(targetKm),
            xpReward = 75,
            progress = 0f,
            completed = false
        )

        // Challenge 3: Beat personal best OR streak goal
        val bestScore = recentTrips.filter { it.endTime != null }.maxOfOrNull { it.score } ?: 0
        val c3 = if (bestScore > 0) {
            WeeklyChallenge(
                id = UUID.randomUUID().toString(),
                type = ChallengeType.BeatPersonalBest(bestScore),
                title = "Beat Your Best",
                description = "Beat your personal best score of $bestScore",
                xpReward = 125,
                progress = 0f,
                completed = false
            )
        } else {
            WeeklyChallenge(
                id = UUID.randomUUID().toString(),
                type = ChallengeType.ScoreThreshold(75, 5),
                title = "Score Hunter",
                description = "Score 75+ on 5 trips",
                xpReward = 125,
                progress = 0f,
                completed = false
            )
        }

        return listOf(c1, c2, c3)
    }

    private fun findWeakestCategory(trips: List<Trip>): ScoreCategory? {
        if (trips.size < 3) return null
        val lastFive = trips.filter { it.endTime != null }.takeLast(5)
        if (lastFive.isEmpty()) return null

        // Average each category
        val accel = lastFive.map { it.scoreAcceleration }.average()
        val brake = lastFive.map { it.scoreBraking }.average()
        val corner = lastFive.map { it.scoreCornering }.average()
        val smooth = lastFive.map { it.scoreSmoothness }.average()
        val consist = lastFive.map { it.scoreConsistency }.average()

        val map = mapOf(
            ScoreCategory.ACCELERATION to accel,
            ScoreCategory.BRAKING to brake,
            ScoreCategory.CORNERING to corner,
            ScoreCategory.SMOOTHNESS to smooth,
            ScoreCategory.CONSISTENCY to consist
        )

        return map.minByOrNull { it.value }?.key
    }
}
