package com.revrank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val challengeRepository: ChallengeRepository
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

            // TODO: earned badges should be loaded from persistence once badge storage lands
            val newlyEarned = badgeEvaluator.evaluate(trip, allTrips, emptyList())
            _newBadges.value = newlyEarned

            val streakBonus = badgeEvaluator.drivingStreak(allTrips)
            val gained = XPCalculator.calculate(trip, streakBonus)
            _xpGained.value = gained

            val oldRank = Rank.fromXp(user.xp)
            val updatedRank = Rank.fromXp(user.xp + gained)
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
