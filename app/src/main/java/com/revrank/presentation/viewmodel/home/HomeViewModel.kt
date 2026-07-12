package com.revrank.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Rank
import com.revrank.domain.model.StreakSystem
import com.revrank.domain.model.WeeklyChallenge
import com.revrank.domain.usecase.WeeklyChallengeGenerator
import com.revrank.presentation.screens.home.LastTripSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weeklyChallengeGenerator: WeeklyChallengeGenerator
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(HomeUiState(
        username = "Player",
        xp = 1250,
        rank = Rank.CRUISER,
        streakDays = 5,
        weeklyTrips = 12,
        weeklyKm = 185.5f,
        challenges = emptyList(),
        lastTrip = null
    ))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        // Simulate loading from repositories
        val today = LocalDate.now()
        val challenges = weeklyChallengeGenerator.generate(listOf()) // empty list for demo

        _uiState.update { it.copy(
            challenges = challenges
        )}
    }

    // Actions
    fun onStartTripClicked() {
        // TODO: Start trip tracking
    }

    fun onViewRanksClicked() {
        // TODO: Navigate to RanksScreen
    }

    fun onLastTripClicked(tripId: String) {
        // TODO: Navigate to trip detail
    }

    // Data class for UI state
    data class HomeUiState(
        val username: String,
        val xp: Int,
        val rank: Rank,
        val streakDays: Int,
        val weeklyTrips: Int,
        val weeklyKm: Float,
        val challenges: List<WeeklyChallenge>,
        val lastTrip: LastTripSummary? // nullable for empty state
    )
}