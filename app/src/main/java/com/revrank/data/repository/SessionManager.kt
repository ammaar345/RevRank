package com.revrank.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.revrank.data.local.DemoData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the signed-in user's id.
 *
 * Returns the real Firebase uid once Google Sign-In completes, and falls back
 * to [DemoData.DEMO_USER_ID] when nobody is signed in so the offline demo seed
 * still renders on Trips / Analytics / Ranks before auth.
 */
@Singleton
class SessionManager @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUserId: String
        get() = auth.currentUser?.uid ?: DemoData.DEMO_USER_ID

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    fun signOut() = auth.signOut()
}
