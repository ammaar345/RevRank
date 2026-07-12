package com.revrank.domain

import com.revrank.utils.TripScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TripScoreCalculatorTest {

    private val calc = TripScoreCalculator()

    @Test
    fun `perfectly smooth driving scores high`() {
        // Zero acceleration, zero lateral, constant speed → all sub-scores maxed
        val window = TripScoreCalculator.SensorWindow(
            accelerations = List(20) { 0f },
            laterals = List(20) { 0f },
            speeds = List(20) { 15f }
        )
        val result = calc.calculateScore(window)
        assertTrue("expected high total, got ${result.total}", result.total >= 90)
        assertEquals(0, result.hardBrakingCount)
        assertEquals(0, result.hardAccelCount)
        assertEquals(0, result.hardCorneringCount)
    }

    @Test
    fun `aggressive driving scores low and counts hard events`() {
        // Heavy accel/brake/corner spikes
        val window = TripScoreCalculator.SensorWindow(
            accelerations = List(20) { if (it % 2 == 0) 0.6f else -0.6f },
            laterals = List(20) { 0.7f },
            speeds = List(20) { if (it % 2 == 0) 5f else 25f }
        )
        val result = calc.calculateScore(window)
        assertTrue("expected low total, got ${result.total}", result.total <= 40)
        assertTrue(result.hardCorneringCount > 0)
    }

    @Test
    fun `empty window does not crash and returns bounded score`() {
        val window = TripScoreCalculator.SensorWindow(emptyList(), emptyList(), emptyList())
        val result = calc.calculateScore(window)
        assertTrue(result.total in 0..100)
    }

    @Test
    fun `all sub-scores stay within 0 to 100`() {
        val window = TripScoreCalculator.SensorWindow(
            accelerations = listOf(0.1f, 0.3f, -0.2f, 0.5f),
            laterals = listOf(0.1f, 0.25f, 0.4f, 0.15f),
            speeds = listOf(10f, 12f, 8f, 15f)
        )
        val r = calc.calculateScore(window)
        listOf(r.total, r.acceleration, r.braking, r.cornering, r.smoothness, r.consistency)
            .forEach { assertTrue("sub-score $it out of range", it in 0..100) }
    }
}
