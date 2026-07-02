package com.revrank.domain.repository

import com.revrank.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for leaderboard data.
 */
interface LeaderboardRepository {
    /**
     * Returns a cold [Flow] that emits the current global top‑100 entries
     * ordered by average score (descending).
     *
     * Consumers should collect in a ViewModelScope.
     */
    fun getGlobalLeaderboard(): Flow<List<LeaderboardEntry>>

    /**
     * Returns a cold [Flow] that emits the current friends leaderboard for the
     * currently authenticated user.
     *
     * Premium feature – caller must ensure the user is entitled (Pro / paid).
     */
    fun getFriendsLeaderboard(): Flow<List<LeaderboardEntry>>

    /**
     * Refresh both streams on demand (pull‑to‑refresh / swipe).
     */
    suspend fun refreshLeaderboards()
}
