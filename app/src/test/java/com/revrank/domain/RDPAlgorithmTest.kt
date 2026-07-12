package com.revrank.domain

import com.revrank.domain.model.GpsPoint
import com.revrank.utils.RDPAlgorithm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RDPAlgorithmTest {

    private fun pt(lat: Double, lng: Double) =
        GpsPoint(lat = lat, lng = lng, timestamp = 0L, speedKmh = 0f, accuracy = 0f)

    @Test
    fun `straight line collapses to two endpoints`() {
        val line = (0..10).map { pt(it * 0.001, it * 0.001) }
        val simplified = RDPAlgorithm.simplify(line, epsilon = 0.00005)
        assertEquals(2, simplified.size)
        assertEquals(line.first(), simplified.first())
        assertEquals(line.last(), simplified.last())
    }

    @Test
    fun `sharp corner is preserved`() {
        // L-shape: the corner point must survive simplification
        val path = listOf(
            pt(0.0, 0.0),
            pt(0.0, 0.005),
            pt(0.0, 0.01),   // along the first leg
            pt(0.005, 0.01), // corner turn
            pt(0.01, 0.01)
        )
        val simplified = RDPAlgorithm.simplify(path, epsilon = 0.00005)
        assertTrue("corner should be kept", simplified.any { it.lat == 0.0 && it.lng == 0.01 })
    }

    @Test
    fun `simplify never grows the point count`() {
        val path = (0..50).map { pt(Math.sin(it.toDouble()) * 0.001, it * 0.001) }
        val simplified = RDPAlgorithm.simplify(path, epsilon = 0.00005)
        assertTrue(simplified.size <= path.size)
        assertTrue(simplified.size >= 2)
    }

    @Test
    fun `tiny input returned as-is`() {
        val two = listOf(pt(0.0, 0.0), pt(0.001, 0.001))
        assertEquals(2, RDPAlgorithm.simplify(two).size)
    }
}
