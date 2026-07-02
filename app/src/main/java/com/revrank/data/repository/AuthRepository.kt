package com.revrank.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.revrank.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Firebase auth returned null user")

            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()

            if (!userDoc.exists()) {
                val newUser = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "email" to (firebaseUser.email ?: ""),
                    "displayName" to (firebaseUser.displayName ?: "Driver"),
                    "username" to (firebaseUser.displayName?.replace(" ", "_")?.lowercase() ?: "user_${System.currentTimeMillis()}"),
                    "rank" to 1,
                    "xp" to 0,
                    "totalTrips" to 0,
                    "totalDistanceKm" to 0.0,
                    "streakDays" to 0,
                    "isPro" to false,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
            }

            Result.success(mapToUser(firebaseUser.uid))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(mapToUser(firebaseUser.uid))
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun claimUsername(uid: String, username: String): Result<Boolean> {
        return try {
            val usernameRef = firestore.collection("usernames").document(username.lowercase())
            val userRef = firestore.collection("users").document(uid)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(usernameRef)
                if (snapshot.exists()) {
                    throw Exception("Username already taken")
                }
                transaction.set(usernameRef, hashMapOf("uid" to uid))
                transaction.update(userRef, "username", username.lowercase())
            }.await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToUser(uid: String): User {
        return User(
            uid = uid,
            username = "",
            displayName = "",
            lastTripDate = null
        )
    }
}
