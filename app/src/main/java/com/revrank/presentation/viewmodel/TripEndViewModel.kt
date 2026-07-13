package com.revrank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.local.dao.BadgeDao
import com.revrank.data.local.entities.BadgeEntity
import com.revrank.data.repository.ChallengeRepository
import com.revrank.data.repository.TripRepository
import com.revrank.data.repository.UserRepository
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Rank
import com.revrank.domain.model.Trip
import com.revrank.domain.model.User
import com.revrank.domain.model.XPCalculator
import com.revrank.domain.usecase.BadgeEvaluator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for trip-end rewards: badges, XP, rank-up, and matching-challenge updates.
 */
@HiltViewModel
class TripEndViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository,
    private val badgeEvaluator: BadgeEvaluator,
    private val challengeRepository: ChallengeRepository,
    private val badgeDao: BadgeDao
) : ViewModel() {

    private val _endedTrip = MutableStateFlow<Trip?>(null)
    val endedTrip: StateFlow<Trip?> = _endedTrip.asStateFlow()

    private val _newBadges = MutableStateFlow<List<BadgeType>>(emptyList())
    val newBadges: StateFlow<List<BadgeType>> = _newBadges.asStateFlow()

    private val _xpGained = MutableStateFlow(0)
    val xpGained: StateFlow<Int> = _xpGained.asStateFlow()

    private val _newRank = MutableStateFlow<String?>(null)
    val newRank: StateFlow<String?> = _newRank.asStateFlow()

    /** Call when a trip has ended to award badges/XP and update matching challenges. */
    fun onTripEnded(trip: Trip, user: User) {
        _endedTrip.value = trip
        viewModelScope.launch {
            val allTrips = tripRepository.getAllTripsForUserFlow(trip.userId).first()

            // Evaluate against already-earned badges so only genuinely new ones fire,
            // then persist them.
            val alreadyEarned = badgeDao.getBadgesForUser(trip.userId).first()
                .mapNotNull { runCatching { BadgeType.valueOf(it.badgeType) }.getOrNull() }
            val newlyEarned = badgeEvaluator.evaluate(trip, allTrips, alreadyEarned)
            _newBadges.value = newlyEarned
            newlyEarned.forEach { badge ->
                badgeDao.insertBadge(
                    BadgeEntity(
                        id = "${trip.userId}_${badge.name}",
                        userId = trip.userId,
                        earnedAt = System.currentTimeMillis(),
                        badgeType = badge.name
                    )
                )
            }

            val streakBonus = badgeEvaluator.drivingStreak(allTrips)
            val gained = XPCalculator.calculate(trip, streakBonus)
            _xpGained.value = gained

            // Rank-up computed from real accumulated XP across all prior finished
            // trips (not the passed-in user stub).
            val priorXp = allTrips
                .filter { it.id != trip.id && it.endTime != null }
                .sumOf { XPCalculator.calculate(it, 0) }
            val oldRank = Rank.fromXp(priorXp)
            val updatedRank = Rank.fromXp(priorXp + gained)
            if (updatedRank != oldRank) {
                _newRank.value = updatedRank.displayName
            }
            try {
                userRepository.addXp(user.uid, gained)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Update any pending challenge acceptances matching this trip's route
            try {
                challengeRepository.updateChallengeScoreForTrip(
                    userId = trip.userId,
                    routeHash = trip.computeRouteHash(),
                    score = trip.score
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun dismissReward() {
        _newBadges.value = emptyList()
        _xpGained.value = 0
        _newRank.value = null
    }
}
