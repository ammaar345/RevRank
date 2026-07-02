package com.revrank.data.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.revrank.domain.model.LeaderboardEntry
import com.revrank.domain.repository.LeaderboardRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Firestore implementation of the [LeaderboardRepository].
 *
 * **Schema assumptions**
 * * `weeklyScores/{uid}` — document per user with fields:
 *   `uid`, `username`, `avgScore`, `totalTrips`, `totalKm`, `rankName`, `rankColorHex`.
 * * `users/{uid}/friends` — subcollection of friend documents for friends queries.
 */
@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LeaderboardRepository {

    companion object {
        private const val TOP_LIMIT = 100L
        private const val WEEKLY_SCORES = "weeklyScores"
    }

    override fun getGlobalLeaderboard(): Flow<List<LeaderboardEntry>> = channelFlow {
        val snapshot = withContext(ioDispatcher) {
            db.collection(WEEKLY_SCORES)
                .orderBy("avgScore", Query.Direction.DESCENDING)
                .limit(TOP_LIMIT)
                .get()
                .await()
        }
        val entries = snapshot.documents.mapIndexed { idx, doc ->
            LeaderboardEntry(
                uid = doc.getString("uid") ?: "",
                username = doc.getString("username") ?: "Unknown",
                avgScore = doc.getLong("avgScore")?.toInt() ?: 0,
                totalTrips = doc.getLong("totalTrips")?.toInt() ?: 0,
                totalKm = doc.getDouble("totalKm") ?: 0.0,
                rankName = doc.getString("rankName") ?: "",
                rankColorHex = doc.getString("rankColorHex") ?: "#00FF41",
                rank = idx + 1
            )
        }
        trySend(entries)
        awaitClose { /* listener‑free one‑shot; nothing to clean */ }
    }

    override fun getFriendsLeaderboard(): Flow<List<LeaderboardEntry>> = channelFlow {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            trySend(emptyList())
            close()
            return@channelFlow
        }

        // 1. Fetch friend UIDs
        val friendsSnapshot = withContext(ioDispatcher) {
            db.collection("users")
                .document(currentUser.uid)
                .collection("friends")
                .get()
                .await()
        }
        val friendUids =
            friendsSnapshot.documents.map { it.id }.toMutableList().apply { add(currentUser.uid) }

        // 2. Fetch weekly score docs for those UIDs in staggered batches (Firestore "in" limit = 10)
        val entries = mutableListOf<LeaderboardEntry>()
        withContext(ioDispatcher) {
            friendUids.chunked(10).forEach { chunk ->
                val chunkSnapshot = db.collection(WEEKLY_SCORES)
                    .whereIn("uid", chunk)
                    .get()
                    .await()
                chunkSnapshot.documents.forEach { doc ->
                    entries.add(
                        LeaderboardEntry(
                            uid = doc.getString("uid") ?: "",
                            username = doc.getString("username") ?: "Unknown",
                            avgScore = doc.getLong("avgScore")?.toInt() ?: 0,
                            totalTrips = doc.getLong("totalTrips")?.toInt() ?: 0,
                            totalKm = doc.getDouble("totalKm") ?: 0.0,
                            rankName = doc.getString("rankName") ?: "",
                            rankColorHex = doc.getString("rankColorHex") ?: "#00FF41",
                            rank = 0 // friends rank uses local sorting
                        )
                    )
                }
            }
        }
        // Sort descending by avgScore and re-assign rank
        val sorted = entries.sortedByDescending { it.avgScore }.mapIndexed { idx, entry ->
            entry.copy(rank = idx + 1)
        }
        trySend(sorted)
        awaitClose { /* one-shot */ }
    }

    override suspend fun refreshLeaderboards() {
        // The current implementation is one-shot per collection.
        // Future: add remote cache busting or Cloud Function trigger here.
    }
}
