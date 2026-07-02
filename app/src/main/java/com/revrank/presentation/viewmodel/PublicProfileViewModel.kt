package com.revrank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the public profile screen. */
data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    /** Load user data by username. */
    fun loadUserByUsername(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val userDoc = firestore.collection("users")
                    .whereEqualTo("username", username)
                    .limit(1)
                    .get()
                    .await()

                if (userDoc.isNotEmpty()) {
                    val user = userDoc[0].toObject(User::class.java) ?: User(
                        uid = "",
                        username = username,
                        displayName = username,
                        rank = 1,
                        xp = 0,
                        totalTrips = 0,
                        totalDistanceKm = 0f,
                        streakDays = 0,
                        lastTripDate = null,
                        isPro = false
                    )
                    _uiState.value = _uiState.value.copy(user = user, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to load user"
                )
            }
        }
    }
}