package com.revrank.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.local.dao.BadgeDao
import com.revrank.data.repository.SessionManager
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Rank
import com.revrank.domain.model.Trip
import com.revrank.domain.model.WeeklyChallenge
import com.revrank.domain.model.XPCalculator
import com.revrank.domain.model.toGradeColor
import com.revrank.domain.usecase.WeeklyChallengeGenerator
import com.revrank.presentation.screens.home.LastTripSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home dashboard state, derived entirely from the signed-in user's real trips
 * (rank/XP/streak/weekly stats/last trip) and persisted badges.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    tripRepository: TripRepository,
    badgeDao: BadgeDao,
    private val session: SessionManager,
    private val weeklyChallengeGenerator: WeeklyChallengeGenerator
) : ViewModel() {

    private val userId = session.currentUserId

    val uiState: StateFlow<HomeUiState> = combine(
        tripRepository.getAllTripsForUserFlow(userId),
        badgeDao.getBadgesForUser(userId)
    ) { trips, badgeEntities ->
        val badges = badgeEntities.mapNotNull {
            runCatching { BadgeType.valueOf(it.badgeType) }.getOrNull()
        }
        buildState(trips, badges)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.EMPTY
    )

    private fun buildState(trips: List<Trip>, badges: List<BadgeType>): HomeUiState {
        val completed = trips.filter { it.endTime != null }.sortedByDescending { it.startTime }

        val totalXp = completed.sumOf { XPCalculator.calculate(it, 0) }
        val rank = Rank.fromXp(totalXp)

        val weekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val weekTrips = completed.filter { it.startTime >= weekAgo }

        val last = completed.firstOrNull()?.let {
            LastTripSummary(
                id = it.id,
                date = DATE_FMT.format(Date(it.startTime)),
                distanceKm = it.distanceKm.toFloat(),
                score = it.score,
                gradeColor = it.score.toGradeColor()
            )
        }

        return HomeUiState(
            username = session.displayName,
            xp = totalXp,
            rank = rank,
            streakDays = currentStreakDays(completed),
            weeklyTrips = weekTrips.size,
            weeklyKm = weekTrips.sumOf { it.distanceKm }.toFloat(),
            totalTrips = completed.size,
            totalKm = completed.sumOf { it.distanceKm }.toFloat(),
            challenges = weeklyChallengeGenerator.generate(completed),
            lastTrip = last,
            badges = badges
        )
    }

    /** Consecutive calendar days (ending at the most recent trip) that have >= 1 trip. */
    private fun currentStreakDays(completed: List<Trip>): Int {
        if (completed.isEmpty()) return 0
        val days = completed.map { dayIndex(it.startTime) }.toSortedSet().toList().reversed()
        var streak = 1
        for (i in 1 until days.size) {
            if (days[i - 1] - days[i] == 1L) streak++ else break
        }
        return streak
    }

    private fun dayIndex(timeMs: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = timeMs }
        return cal.get(Calendar.YEAR) * 366L + cal.get(Calendar.DAY_OF_YEAR)
    }

    data class HomeUiState(
        val username: String,
        val xp: Int,
        val rank: Rank,
        val streakDays: Int,
        val weeklyTrips: Int,
        val weeklyKm: Float,
        val totalTrips: Int,
        val totalKm: Float,
        val challenges: List<WeeklyChallenge>,
        val lastTrip: LastTripSummary?,
        val badges: List<BadgeType>
    ) {
        companion object {
            val EMPTY = HomeUiState(
                username = "Driver",
                xp = 0,
                rank = Rank.LEARNER,
                streakDays = 0,
                weeklyTrips = 0,
                weeklyKm = 0f,
                totalTrips = 0,
                totalKm = 0f,
                challenges = emptyList(),
                lastTrip = null,
                badges = emptyList()
            )
        }
    }

    companion object {
        private val DATE_FMT = SimpleDateFormat("MMM d · HH:mm", Locale.getDefault())
    }
}
