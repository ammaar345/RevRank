package com.revrank.data.local

import com.google.gson.Gson
import com.revrank.data.local.entities.TripEntity
import com.revrank.domain.model.GForcePoint
import com.revrank.domain.model.GpsPoint
import kotlin.math.sin
import kotlin.random.Random

/**
 * Seed data for offline demo / first-run so the Trips, Analytics, Ranks and
 * Home screens have content before any real trips or cloud sync exist.
 *
 * Uses [DEMO_USER_ID] — the same placeholder id the ViewModels read today
 * (until real auth is wired). Seeded only when the trips table is empty.
 */
object DemoData {

    const val DEMO_USER_ID = "current_user_id"

    private val gson = Gson()
    private const val DAY_MS = 24 * 60 * 60 * 1000L

    private data class Seed(
        val name: String,
        val daysAgo: Int,
        val distanceKm: Float,
        val durationMin: Int,
        val score: Int,
        val accel: Int,
        val brake: Int,
        val corner: Int,
        val smooth: Int,
        val consist: Int,
        val maxSpeed: Float
    )

    private val seeds = listOf(
        Seed("Home → Work", 0, 24.2f, 34, 91, 88, 85, 93, 90, 94, 118f),
        Seed("Evening Drive", 1, 18.6f, 28, 78, 74, 79, 82, 76, 80, 96f),
        Seed("Weekend Stretch", 3, 42.1f, 51, 95, 93, 96, 91, 97, 94, 132f),
        Seed("Grocery Run", 4, 8.4f, 16, 84, 82, 88, 79, 86, 85, 74f),
        Seed("Coastal Loop", 6, 63.8f, 78, 88, 85, 84, 90, 88, 86, 141f),
        Seed("Morning Commute", 8, 21.0f, 31, 72, 68, 70, 75, 71, 74, 88f)
    )

    fun trips(now: Long = System.currentTimeMillis()): List<TripEntity> = seeds.map { s ->
        val start = now - s.daysAgo * DAY_MS
        val end = start + s.durationMin * 60_000L
        TripEntity(
            id = "demo_${s.name.hashCode()}",
            userId = DEMO_USER_ID,
            startTime = start,
            endTime = end,
            distanceKm = s.distanceKm,
            maxSpeedKmh = s.maxSpeed,
            avgSpeedKmh = if (s.durationMin > 0) s.distanceKm / (s.durationMin / 60f) else 0f,
            score = s.score,
            scoreAcceleration = s.accel,
            scoreBraking = s.brake,
            scoreCornering = s.corner,
            scoreSmoothness = s.smooth,
            scoreConsistency = s.consist,
            routeName = s.name,
            shareImagePath = null,
            isSynced = true, // demo rows should never try to sync to Firestore
            gpsPointsJson = gson.toJson(fakeRoute(start)),
            gForcePointsJson = gson.toJson(fakeGForce(start, s.score))
        )
    }

    /** A gentle wavy fake route so Route Replay has a polyline to draw. */
    private fun fakeRoute(start: Long): List<GpsPoint> =
        (0..60).map { i ->
            GpsPoint(
                lat = 37.7749 + i * 0.0006,
                lng = -122.4194 + sin(i * 0.2) * 0.0015,
                timestamp = start + i * 15_000L,
                speedKmh = (40 + sin(i * 0.3) * 25).toFloat(),
                accuracy = 5f
            )
        }

    /** Fake G-force samples clustered by how good the trip was. */
    private fun fakeGForce(start: Long, score: Int): List<GForcePoint> {
        val spread = (100 - score) / 100f * 0.6f + 0.1f
        return (0..80).map { i ->
            GForcePoint(
                timestamp = start + i * 10_000L,
                lateralG = (Random.nextFloat() - 0.5f) * 2 * spread,
                longitudinalG = (Random.nextFloat() - 0.5f) * 2 * spread,
                scoreAtMoment = score
            )
        }
    }
}
