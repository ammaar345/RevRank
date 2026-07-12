package com.revrank.domain

import com.revrank.domain.model.Trip
import com.revrank.domain.model.XPCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XPCalculatorTest {

    private fun trip(score: Int, distanceKm: Double) = Trip(
        id = "t",
        userId = "u",
        startTime = 0L,
        endTime = 1000L,
        distanceKm = distanceKm,
        score = score,
        scoreAcceleration = score,
        scoreBraking = score,
        scoreCornering = score,
        scoreSmoothness = score,
        scoreConsistency = score,
        shareImagePath = null
    )

    @Test
    fun `short trip clamps distance factor to at least one`() {
        // distance 2km → factor coerced to 1.0; base = 80 * 1 = 80, no streak
        assertEquals(80, XPCalculator.calculate(trip(80, 2.0), streakBonus = 0))
    }

    @Test
    fun `distance scales base xp`() {
        // 20km → factor 2.0; base = 80 * 2 = 160
        assertEquals(160, XPCalculator.calculate(trip(80, 20.0), streakBonus = 0))
    }

    @Test
    fun `perfect score adds bonus`() {
        // score 100, 10km → factor 1.0; base 100 + perfect 50 = 150
        assertEquals(150, XPCalculator.calculate(trip(100, 10.0), streakBonus = 0))
    }

    @Test
    fun `streak bonus multiplies`() {
        // base 80, streak 5 → x1.5 = 120
        assertEquals(120, XPCalculator.calculate(trip(80, 2.0), streakBonus = 5))
    }

    @Test
    fun `xp is never negative`() {
        assertTrue(XPCalculator.calculate(trip(0, 0.0), streakBonus = 0) >= 0)
    }
}
