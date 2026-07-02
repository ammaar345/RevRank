package com.revrank.presentation.viewmodel.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.domain.model.ChallengeAcceptance
import com.revrank.domain.model.RouteChallenge
import com.revrank.data.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    // UI State
    private val _challenge = MutableStateFlow<RouteChallenge?>(null)
    val challenge: StateFlow<RouteChallenge?> = _challenge.asStateFlow()

    private val _isAccepting = MutableStateFlow(false)
    val isAccepting: StateFlow<Boolean> = _isAccepting.asStateFlow()

    private val _isCreated = MutableStateFlow(false)
    val isCreated: StateFlow<Boolean> = _isCreated.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Load a challenge by ID
    fun loadChallenge(challengeId: String) {
        viewModelScope.launch {
            try {
                _isAccepting.value = false
                _isAccepted.value = false
                _error.value = null
                val challenge = challengeRepository.getChallenge(challengeId)
                _challenge.value = challenge
            } catch (e: Exception) {
                _error.value = "Failed to load challenge: ${e.localizedMessage}"
                _challenge.value = null
            }
        }
    }

    // Accept the challenge
    fun acceptChallenge(challengeId: String, userId: String, username: String) {
        viewModelScope.launch {
            _isAccepting.value = true
            _error.value = null
            _isAccepted.value = false
            try {
                // Create an acceptance record
                val acceptance = ChallengeAcceptance(
                    uid = userId,
                    username = username,
                    score = null, // Not yet completed
                    completedAt = null
                )
                challengeRepository.acceptChallenge(challengeId, acceptance)
                // Mark as accepted successfully
                _isAccepted.value = true
            } catch (e: Exception) {
                _error.value = "Failed to accept challenge: ${e.localizedMessage}"
                _isAccepting.value = false
            }
        }
    }

    /** Call when the user completes a trip that matches this challenge to update their score */
    fun updateChallengeScore(challengeId: String, userId: String, score: Int) {
        viewModelScope.launch {
            try {
                challengeRepository.updateChallengeAcceptanceScore(challengeId, userId, score)
                // Optionally, we could reload the challenge to reflect the updated score
            } catch (e: Exception) {
                _error.value = "Failed to update score: ${e.localizedMessage}"
            }
        }
    }

    /** Create a new route challenge from a trip and user */
    fun createChallenge(trip: com.revrank.domain.model.Trip, user: com.revrank.domain.model.User) {
        viewModelScope.launch {
            _isAccepting.value = false // reuse for creating?
            _error.value = null
            _isCreated.value = false
            try {
                val challengeId = UUID.randomUUID().toString()
                val routeHash = trip.computeRouteHash()
                val now = System.currentTimeMillis()
                val oneWeekLater = now + TimeUnit.DAYS.toMillis(7)
                val challenge = RouteChallenge(
                    id = challengeId,
                    creatorUid = user.uid,
                    creatorUsername = user.username,
                    creatorScore = trip.score,
                    routeHash = routeHash,
                    tripId = trip.id,
                    createdAt = now,
                    expiresAt = oneWeekLater
                )
                challengeRepository.createRouteChallenge(challenge)
                _isCreated.value = true
                // Optionally, we could emit the challengeId via a callback or state flow
                _challenge.value = challenge
            } catch (e: Exception) {
                _error.value = "Failed to create challenge: ${e.localizedMessage}"
                _isAccepting.value = false
            }
        }
    }

    // Clear errors
    fun clearError() {
        _error.value = null
    }
}