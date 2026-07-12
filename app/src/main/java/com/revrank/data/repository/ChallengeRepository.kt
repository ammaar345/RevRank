package com.revrank.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.revrank.domain.model.ChallengeAcceptance
import com.revrank.domain.model.RouteChallenge
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    private val challengesCollection = db.collection("challenges")

    private fun challengesDocument(challengeId: String) = challengesCollection.document(challengeId)

    private fun acceptancesSubcollection(challengeId: String) =
        challengesDocument(challengeId).collection("acceptances")

    /** Save a new route challenge */
    suspend fun createRouteChallenge(challenge: RouteChallenge) {
        val challengeData = mapOf(
            "id" to challenge.id,
            "creatorUid" to challenge.creatorUid,
            "creatorUsername" to challenge.creatorUsername,
            "creatorScore" to challenge.creatorScore,
            "routeHash" to challenge.routeHash,
            "tripId" to challenge.tripId,
            "createdAt" to challenge.createdAt,
            "expiresAt" to challenge.expiresAt
        )
        challengesDocument(challenge.id).set(challengeData, SetOptions.merge()).await()
    }

    /** Get a challenge by its ID */
    suspend fun getChallenge(challengeId: String): RouteChallenge? =
        challengesDocument(challengeId).get().await()
            .toObject(RouteChallenge::class.java)

    /** Accept a challenge */
    suspend fun acceptChallenge(challengeId: String, acceptance: ChallengeAcceptance) {
        val acceptanceData = mapOf(
            "uid" to acceptance.uid,
            "username" to acceptance.username,
            "score" to acceptance.score,
            "completedAt" to acceptance.completedAt
        )
        acceptancesSubcollection(challengeId).document(acceptance.uid)
            .set(acceptanceData, SetOptions.merge()).await()
    }

    /** Update the score of an acceptance (when the user completes the challenge) */
    suspend fun updateChallengeAcceptanceScore(challengeId: String, userId: String, score: Int) {
        acceptancesSubcollection(challengeId).document(userId).update("score", score).await()
    }

    /**
     * Update the score for any acceptance matching the given userId and routeHash.
     * Called when a trip ends to update any pending challenges that match the trip's route.
     */
    suspend fun updateChallengeScoreForTrip(userId: String, routeHash: String, score: Int) {
        // Could be optimized with queries on expiresAt > now; fine for MVP volumes.
        val challengesSnapshot = challengesCollection.get().await()
        for (challengeDoc in challengesSnapshot.documents) {
            val challenge = challengeDoc.toObject(RouteChallenge::class.java) ?: continue
            val now = System.currentTimeMillis()
            if (challenge.expiresAt in 1 until now) {
                continue // expired
            }
            val acceptanceDoc = acceptancesSubcollection(challenge.id)
                .document(userId)
                .get()
                .await()
            if (acceptanceDoc.exists()) {
                val acceptance = acceptanceDoc.toObject(ChallengeAcceptance::class.java) ?: continue
                // If the acceptance has no score yet and the routeHash matches, update the score
                if (acceptance.score == null && challenge.routeHash == routeHash) {
                    updateChallengeAcceptanceScore(challenge.id, userId, score)
                }
            }
        }
    }

    /** Get all acceptances for a challenge */
    suspend fun getChallengeAcceptances(challengeId: String): List<ChallengeAcceptance> =
        acceptancesSubcollection(challengeId).get().await()
            .documents.mapNotNull { it.toObject(ChallengeAcceptance::class.java) }

    /** Delete a challenge and its acceptances */
    suspend fun deleteChallenge(challengeId: String) {
        acceptancesSubcollection(challengeId).get().await().documents.forEach { doc ->
            doc.reference.delete()
        }
        challengesDocument(challengeId).delete().await()
    }
}
