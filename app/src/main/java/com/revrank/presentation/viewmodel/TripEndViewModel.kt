package com.revrank.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.contextAware
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Challenge
import com.revrank.domain.model.RouteChallenge
import com.revrank.domain.model.Trip
import com.revrank.domain.model.User
import com.revrank.domain.usecase.BadgeEvaluator
import com.revrank.domain.model.XPCalculator
import com.revrank.data.repository.ChallengeRepository
import com.revrank.data.repository.TripRepository
import com.revrank.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * ViewModel for handling trip end logic: ending trips, awarding badges/XP, rank-up, sharing.
 * Also updates any pending challenge acceptances that match the trip's route.
 */
@HiltViewModel
class TripEndViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val badgeEvaluator: BadgeEvaluator,
    private val challengeRepository: ChallengeRepository // Added for challenge score updates
) : ViewModel() {

    // Expose the ended trip for UI to collect
    var endedTrip by mutableStateOf<Trip?>(null)
        private set

    // UI state
    var newBadges by mutableStateOf<listOf<BadgeType>> = emptyList()
        private set
    var xpGained by mutableStateOf<Int> = 0
        private set
    var newRank by mutableStateOf<String?>(null)
        private set
    var shareUri by mutableStateOf<android.net.Uri?>(null)
        private set

    init {
        // Listen for ended trips from repository (could also be triggered from LiveHudScreen)
        viewModelScope.launch {
            tripRepository.getEndedTripsFlow().collectLatest { trip -> 
                if (trip != null) {
                    handleTripEnded(trip)
                }
            }
        }
    }

    private fun handleTripEnded(trip: Trip) {
        endedTrip = trip
        val allTrips = tripRepository.getAllTripsForUser(trip.userId)
        val currentUser = userRepository.getCurrentUser() ?: return
        val existingBadges = currentUser.badges // Assume we store badge IDs as strings in User

        // Evaluate new badges
        val newlyEarned = badgeEvaluator.evaluate(trip, allTrips, existingBadges)
        newBadges = newlyEarned

        // Calculate XP (streak bonus from drivingStreak in BadgeEvaluator)
        val streakBonus = badgeEvaluator.drivingStreak(allTrips)
        xpGained = XPCalculator.calculate(trip, streakBonus)

        // Award XP and check rank up
        val updatedUser = currentUser.copy(
            xp = currentUser.xp + xpGained,
            badges = (currentUser.badges + newlyEarned.map { it.id }).distinct()
        )
        userRepository.updateUser(updatedUser)

        val oldRank = currentUser.rank
        val newRankObj = userRepository.getCurrentUser()?.rank ?: currentUser.rank
        if (oldRank != newRankObj) {
            newRank = newRankObj.displayName
        }

        // Update any challenge acceptances that match this trip's route
        val routeHash = trip.computeRouteHash()
        viewModelScope.launch {
            try {
                challengeRepository.updateChallengeScoreForTrip(
                    userId = trip.userId,
                    routeHash = routeHash,
                    score = trip.score
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Optionally, we could surface this error, but for now just log.
            }
        }

        // Generate share card (non-blocking)
        viewModelScope.launch {
            try {
                // This would be injected or use a helper; for now, assume we have access
                val bitmap = com.revrank.presentation.screens.share.ShareCardGenerator.generate(trip, currentUser)
                // Save to cache and get Uri via FileProvider (implementation omitted for brevity)
                // shareUri = ... 
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markTripAsShared(tripId: String) {
        tripRepository.markTripAsShared(tripId)
        // Refetch trip to update UI if needed
    }

    fun dismissReward() {
        // Reset UI state if needed
        newBadges = emptyList()
        xpGained = 0
        newRank = null
    }
}