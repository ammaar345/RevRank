package com.revrank.data.repository

import com.revrank.data.local.entities.TripEntity
import com.revrank.domain.model.GForcePoint
import com.revrank.domain.model.GpsPoint
import com.revrank.domain.model.Trip
import com.revrank.data.local.RevRankDatabase
import com.revrank.data.local.dao.TripDao
import com.revrank.data.remote.FirestoreTrip
import com.revrank.data.remote.SyncManager
import com.revrank.utils.RDPAlgorithm
import com.revrank.utils.TripScoreCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for trip data.
 * Handles the interaction between local Room database and remote Firestore via SyncManager.
 */
@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val syncManager: SyncManager
) {

    /**
     * Start a new trip for the given user.
     * Initializes a trip entity with default values (empty points lists, zeroed metrics).
     */
    suspend fun startTrip(userId: String): Trip {
        val tripEntity = TripEntity(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            startTime = System.currentTimeMillis(),
            endTime = null,
            distanceKm = 0f,
            maxSpeedKmh = 0f,
            avgSpeedKmh = 0f,
            score = 0,
            scoreAcceleration = 0,
            scoreBraking = 0,
            scoreCornering = 0,
            scoreSmoothness = 0,
            scoreConsistency = 0,
            routeName = null,
            shareImagePath = null,
            isSynced = false,
            gpsPointsJson = null, // empty list
            gForcePointsJson = null // empty list
        )
        tripDao.insertTrip(tripEntity)
        return tripEntity.toDomain()
    }

    /**
     * End a trip and persist the final state.
     * 
     * @param tripId The ID of the trip to end
     * @param finalScore The calculated score breakdown for the trip
     * @param gpsPoints Raw GPS points collected during the trip (from FirestoreTrip model)
     * @param gForcePoints G-force points collected during the trip (downsampled to 1 per 500ms)
     * @return The completed trip domain model, or null if the trip was too short (<500m) and was deleted
     */
    suspend fun endTrip(
        tripId: String,
        finalScore: TripScoreCalculator.ScoreBreakdown,
        gpsPoints: List<GpsPoint>,
        gForcePoints: List<GForcePoint>
    ): Trip? {
        val trip = tripDao.getTripById(tripId) ?: throw IllegalArgumentException("Trip not found: $tripId")
        
        // Check minimum distance: if less than 500m, delete the trip and return null
        if (trip.distanceKm < 0.5f) { // 0.5 km = 500 meters
            tripDao.deleteTrip(trip.id)
            return null
        }
        
        // Apply Ramer-Douglas-Peucker polyline compression (~300 points per trip)
        val compressedGpsPoints = RDPAlgorithm.simplify(gpsPoints, epsilon = 0.00005)
        val trimmedGpsPoints = if (compressedGpsPoints.size > 2000) {
            compressedGpsPoints.take(2000)
        } else {
            compressedGpsPoints
        }

        // Downsample G-force points: keep ~1 per second for visualizer
        val trimmedGForcePoints = if (gForcePoints.size > 1000) {
            gForcePoints.filterIndexed { index, _ -> index % 2 == 0 }.take(1000)
        } else {
            gForcePoints
        }
        
        // Update timing and score
        val endTime = System.currentTimeMillis()
        val durationSec = (endTime - trip.startTime) / 1000
        val avgSpeed = if (durationSec > 0) trip.distanceKm / (durationSec / 3600f) else 0f

        val updated = trip.copy(
            endTime = endTime,
            score = finalScore.total,
            scoreAcceleration = finalScore.acceleration,
            scoreBraking = finalScore.braking,
            scoreCornering = finalScore.cornering,
            scoreSmoothness = finalScore.smoothness,
            scoreConsistency = finalScore.consistency,
            avgSpeedKmh = avgSpeed,
            gpsPointsJson = TripEntity.gpsPointsToJson(trimmedGpsPoints),
            gForcePointsJson = TripEntity.gForcePointsToJson(trimmedGForcePoints)
        )
        
        // Persist the updated trip
        tripDao.updateTrip(updated)
        
        // Sync to Firestore (will include the JSON point data)
        syncManager.syncTrip(updated)
        
        // Return the domain model
        return updated.toDomain()
    }

    /**
     * Update the score of an active trip in real-time (called during tracking).
     */
    suspend fun updateLiveScore(tripId: String, score: Int) {
        val trip = tripDao.getTripById(tripId) ?: return
        val updated = trip.copy(score = score)
        tripDao.updateTrip(updated)
    }

    /**
     * Persist live distance / speed / score onto the active trip while tracking,
     * so the final endTrip() sees the real distance (its < 500m guard reads the
     * row's distanceKm).
     */
    suspend fun updateTripProgress(
        tripId: String,
        distanceKm: Float,
        maxSpeedKmh: Float,
        score: Int
    ) {
        val trip = tripDao.getTripById(tripId) ?: return
        tripDao.updateTrip(
            trip.copy(
                distanceKm = distanceKm,
                maxSpeedKmh = maxOf(trip.maxSpeedKmh, maxSpeedKmh),
                score = score
            )
        )
    }

    /**
     * Update the stored GPS and G-force points for a trip.
     * Called periodically during tracking to avoid losing data if the app is killed.
     */
    suspend fun updateTripPoints(
        tripId: String,
        gpsPoints: List<GpsPoint>,
        gForcePoints: List<GForcePoint>
    ) {
        val trip = tripDao.getTripById(tripId) ?: return
        
        // Compress and trim GPS points
        val compressedGpsPoints = RDPAlgorithm.simplify(gpsPoints, epsilon = 0.00005)
        val trimmedGpsPoints = if (compressedGpsPoints.size > 2000) {
            compressedGpsPoints.take(2000)
        } else {
            compressedGpsPoints
        }

        // Downsample G-force points
        val trimmedGForcePoints = if (gForcePoints.size > 1000) {
            gForcePoints.filterIndexed { index, _ -> index % 2 == 0 }.take(1000)
        } else {
            gForcePoints
        }
        
        val updated = trip.copy(
            gpsPointsJson = TripEntity.gpsPointsToJson(trimmedGpsPoints),
            gForcePointsJson = TripEntity.gForcePointsToJson(trimmedGForcePoints)
        )
        tripDao.updateTrip(updated)
    }

    /**
     * Get a flow of the active trip (if any) for the given user.
     * An active trip is one that has not yet ended (endTime == null).
     */
    fun getActiveTripFlow(userId: String): Flow<Trip?> = tripDao.getTripsForUser(userId)
        .map { list ->
            list.firstOrNull { it.endTime == null }?.toDomain()
        }

    /**
     * Get a flow of all trips for the given user (used for Pro features like unlimited history).
     * For free users, this should be limited in the UI layer (last 30 days, max 10 items).
     */
    fun getAllTripsForUserFlow(userId: String): Flow<List<Trip>> = tripDao.getTripsForUser(userId)
        .map { list ->
            list.map { it.toDomain() }
        }

    /** Push any locally-stored unsynced trips to Firestore. */
    suspend fun syncUnsyncedTrips() = syncManager.syncUnsyncedTrips()
}

private fun TripEntity.toDomain() = Trip(
    id = id,
    userId = userId,
    startTime = startTime,
    endTime = endTime,
    distanceKm = distanceKm.toDouble(),
    durationSec = ((endTime ?: startTime) - startTime) / 1000,
    maxSpeedKmh = maxSpeedKmh.toDouble(),
    avgSpeedKmh = avgSpeedKmh.toDouble(),
    score = score,
    scoreAcceleration = scoreAcceleration,
    scoreBraking = scoreBraking,
    scoreCornering = scoreCornering,
    scoreSmoothness = scoreSmoothness,
    scoreConsistency = scoreConsistency,
    routeName = routeName,
    shareImagePath = shareImagePath,
    isSynced = isSynced,
    wasShared = wasShared,
    gpsPoints = gpsPoints, // uses the converted property from TripEntity
    gForcePoints = gForcePoints // uses the converted property from TripEntity
)