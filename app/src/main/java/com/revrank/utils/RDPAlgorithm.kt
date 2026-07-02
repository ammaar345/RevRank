package com.revrank.utils

import com.revrank.data.local.entities.GpsPoint

/**
 * Ramer-Douglas-Peucker algorithm for polyline simplification.
 * Reduces GPS point count while preserving shape.
 *
 * Target: compress 3000+ raw GPS points to ~300 points per trip
 * while retaining enough fidelity for route replay.
 */
object RDPAlgorithm {

    /**
     * Simplify a list of GPS points using the RDP algorithm.
     * @param points Raw GPS points (must be in chronological order)
     * @param epsilon Perpendicular distance threshold in degrees
     *        (~0.00005 ≈ 5m at mid-latitudes). Increase for more compression.
     * @return Simplified list of points
     */
    fun simplify(points: List<GpsPoint>, epsilon: Double = 0.00005): List<GpsPoint> {
        if (points.size < 3) return points

        // Find point with max perpendicular distance
        val first = points.first()
        val last = points.last()
        var maxDist = 0.0
        var maxIdx = 0

        for (i in 1 until points.lastIndex) {
            val d = perpendicularDistance(
                points[i].lat, points[i].lng,
                first.lat, first.lng,
                last.lat, last.lng
            )
            if (d > maxDist) {
                maxDist = d
                maxIdx = i
            }
        }

        return if (maxDist > epsilon) {
            // Recursively simplify both halves
            val left = simplify(points.subList(0, maxIdx + 1), epsilon)
            val right = simplify(points.subList(maxIdx, points.size), epsilon)
            // Combine: drop last of left (duplicates first of right)
            left.dropLast(1) + right
        } else {
            // Collapse to endpoints
            listOf(first, last)
        }
    }

    /**
     * Perpendicular distance of point p from line formed by p1-p2.
     */
    private fun perpendicularDistance(
        px: Double, py: Double,
        x1: Double, y1: Double,
        x2: Double, y2: Double
    ): Double {
        val dx = x2 - x1
        val dy = y2 - y1
        val lengthSq = dx * dx + dy * dy
        if (lengthSq == 0.0) {
            // p1 == p2: return Euclidean distance
            val ex = px - x1
            val ey = py - y1
            return Math.sqrt(ex * ex + ey * ey)
        }
        // Project p onto line p1-p2
        val t = ((px - x1) * dx + (py - y1) * dy) / lengthSq
        val clampedT = t.coerceIn(0.0, 1.0)
        val projX = x1 + clampedT * dx
        val projY = y1 + clampedT * dy
        val ex = px - projX
        val ey = py - projY
        return Math.sqrt(ex * ex + ey * ey)
    }
}
