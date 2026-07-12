package com.revrank.presentation.viewmodel

import android.graphics.Color.parseColor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.domain.model.LeaderboardEntry
import com.revrank.domain.repository.LeaderboardRepository
import com.revrank.presentation.statemanagement.LocalProStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** UI state for the leaderboard screen. */
data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val globalEntries: List<LeaderboardEntry> = emptyList(),
    val friendsEntries: List<LeaderboardEntry> = emptyList(),
    val currentUserRank: LeaderboardEntry? = null,
    val isPro: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val proStatus: LocalProStatus
) : ViewModel() {

    private fun isProUser(): Boolean = proStatus.current.value

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboards()
    }

    /** Load both global and friends leaderboards on launch */
    fun loadLeaderboards() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isPro = isProUser())
            try {
                // Global
                leaderboardRepository.getGlobalLeaderboard().collectLatest { global ->
                    _uiState.value = _uiState.value.copy(
                        globalEntries = global,
                        isLoading = _uiState.value.friendsEntries.isEmpty()
                    )
                }
                // Friends (Pro)
                if (isProUser()) {
                    leaderboardRepository.getFriendsLeaderboard().collectLatest { friends ->
                        _uiState.value = _uiState.value.copy(
                            friendsEntries = friends,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to load leaderboard"
                )
            }
        }
    }

    /** Pull-to-refresh trigger */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                leaderboardRepository.refreshLeaderboards()
                // Re-collect latest data
                loadLeaderboards()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Refresh failed"
                )
            }
        }
    }
}
