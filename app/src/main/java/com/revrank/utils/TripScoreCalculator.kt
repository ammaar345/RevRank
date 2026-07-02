package com.revrank.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Calculates driving scores from sensor data windows.
 * Based on longitudinal/lateral acceleration and speed consistency.
 */
class TripScoreCalculator {

    /**
     * Sensor data window collected over a short period (e.g., 5 seconds).
     */
    data class SensorWindow(
        val accelerations: List<Float>,   // longitudinal g-force (forward/backward)
        val laterals: List<Float>,        // lateral g-force (left/right)
        val speeds: List<Float>           // speed in m/s
    )

    /**
     * Result of scoring a sensor window.
     */
    data class ScoreBreakdown(
        val total: Int,
        val acceleration: Int,
        val braking: Int,
        val cornering: Int,
        val smoothness: Int,
        val consistency: Int,
        val hardBrakingCount: Int,
        val hardAccelCount: Int,
        val hardCorneringCount: Int
    )

    /**
     * Calculate scores for a given sensor window.
     * Each sub-score is 0-100, weighted equally in final score.
     *
     * Thresholds (in g-force):
     *   - Acceleration: good < 0.15g, ok < 0.25g, bad >= 0.35g
     *   - Braking:      good < -0.15g, ok < -0.25g, bad <= -0.35g
     *   - Cornering:    |lateral| < 0.20g good, < 0.30g ok, >= 0.40g bad
     */
    fun calculateScore(window: SensorWindow): ScoreBreakdown {
        val accelScores = window.accelerations.map { scoreAcceleration(it) }
        val brakeScores = window.accelerations.map { scoreBraking(it) }
        val cornerScores = window.laterals.map { scoreCornering(it) }
        val smoothScore = scoreSmoothness(window.speeds)
        val consistScore = scoreConsistency(window.speeds)

        val acceleration = average(accelScores)
        val braking = average(brakeScores)
        val cornering = average(cornerScores)

        val hardAccelCount = window.accelerations.count { it > 0.35f }
        val hardBrakeCount = window.accelerations.count { it < -0.35f }
        val hardCornerCount = window.laterals.count { abs(it) >= 0.40f }

        val total = (acceleration + braking + cornering + smoothScore + consistScore) / 5

        return ScoreBreakdown(
            total = total,
            acceleration = acceleration,
            braking = braking,
            cornering = cornering,
            smoothness = smoothScore,
            consistency = consistScore,
            hardBrakingCount = hardBrakeCount,
            hardAccelCount = hardAccelCount,
            hardCorneringCount = hardCornerCount
        )
    }

    private fun scoreAcceleration(g: Float): Int {
        // Positive g = acceleration
        val gAbs = abs(g)
        return when {
            gAbs < 0.15 -> 100
            gAbs < 0.25 -> 75
            gAbs < 0.35 -> 50
            else -> 25
        }
    }

    private fun scoreBraking(g: Float): Int {
        // Negative g = braking (deceleration)
        val gAbs = abs(g)
        return when {
            gAbs < 0.15 -> 100
            gAbs < 0.25 -> 75
            gAbs < 0.35 -> 50
            else -> 25
        }
    }

    private fun scoreCornering(g: Float): Int {
        val gAbs = abs(g)
        return when {
            gAbs < 0.20 -> 100
            gAbs < 0.30 -> 75
            gAbs < 0.40 -> 50
            else -> 25
        }
    }

    private fun scoreSmoothness(speeds: List<Float>): Int {
        if (speeds.isEmpty()) return 100
        val avg = speeds.average()
        val variance = speeds.map { (it - avg).pow(2) }.average()
        val stdDev = sqrt(variance.toDouble()).toFloat()
        // Lower stdDev = smoother
        // 0 m/s^2 variance -> 100, 2+ m/s^2 -> 0
        return when {
            stdDev < 0.5 -> 100
            stdDev < 1.0 -> 75
            stdDev < 1.5 -> 50
            else -> 25
        }
    }

    private fun scoreConsistency(speeds: List<Float>): Int {
        if (speeds.isEmpty()) return 100
        val avg = speeds.average()
        val variance = speeds.map { (it - avg).pow(2) }.average()
        // Consistency = how close speeds stay to average
        // 0 variance -> 100, high variance -> 0
        // Normalize: assume max reasonable variance is (10 m/s)^2 = 100
        val normalized = 1.0f - (variance / 100.0f).coerceIn(0.0f, 1.0f)
        return (normalized * 100).toInt()
    }

    private fun average(list: List<Int>): Int {
        if (list.isEmpty()) return 100
        return (list.average()).roundToInt()
    }
}