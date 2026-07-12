package com.revrank.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class UserRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /** Returns the UID of the currently signed-in user, or null if no user is signed in. */
    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    /**
     * Applies a referral bonus to both the referrer and the referee.
     * Gives 100 XP to each, and records the referral relationship.
     * @param referrerId The UID of the user who referred (from the referral code).
     * @param refereeId The UID of the user who accepted the referral (current user).
     */
    suspend fun applyReferralBonus(referrerId: String, refereeId: String) {
        val batch = db.batch()
        val referrerRef = db.collection("users").document(referrerId)
        val refereeRef = db.collection("users").document(refereeId)

        // Update referrer: +100 XP
        batch.update(referrerRef, "xp", FieldValue.increment(100))
        // Update referee: +100 XP, set referredBy and hasUsedReferral
        batch.update(refereeRef, mapOf(
            "xp" to FieldValue.increment(100),
            "referredBy" to referrerId,
            "hasUsedReferral" to true
        ))

        // Commit the batch
        batch.commit().await()
    }

    /** Adds XP to a user's Firestore document. */
    suspend fun addXp(uid: String, amount: Int) {
        if (uid.isBlank() || amount <= 0) return
        db.collection("users").document(uid)
            .update("xp", FieldValue.increment(amount.toLong()))
            .await()
    }
}