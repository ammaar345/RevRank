package com.revrank.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.revrank.data.local.dao.TripDao
import com.revrank.data.local.entities.TripEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val tripDao: TripDao
) {
    suspend fun syncTrip(trip: TripEntity): Result<Unit> {
        return try {
            val firestoreTrip = FirestoreTrip(
                id = trip.id,
                userId = trip.userId,
                startTime = trip.startTime,
                endTime = trip.endTime,
                distanceKm = trip.distanceKm.toDouble(),
                maxSpeedKmh = trip.maxSpeedKmh.toDouble(),
                avgSpeedKmh = trip.avgSpeedKmh.toDouble(),
                score = trip.score,
                scoreAcceleration = trip.scoreAcceleration,
                scoreBraking = trip.scoreBraking,
                scoreCornering = trip.scoreCornering,
                scoreSmoothness = trip.scoreSmoothness,
                scoreConsistency = trip.scoreConsistency,
                routeName = trip.routeName,
                shareImagePath = trip.shareImagePath
            )

            firestore.collection("trips")
                .document(trip.id)
                .set(firestoreTrip)
                .await()

            tripDao.updateTrip(trip.copy(isSynced = true))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncUnsyncedTrips() {
        val unsyncedTrips = tripDao.getUnsyncedTrips()
        for (trip in unsyncedTrips) {
            syncTrip(trip)
        }
    }
}
