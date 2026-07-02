package com.revrank.presentation.viewmodel.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    // We'll get the current user ID from the auth repository, but for simplicity we'll use a placeholder.
    // In a real app, we would inject the AuthRepository or UserRepository to get the current user ID.
    private val userId = "current_user_id" // TODO: Replace with actual user ID from auth

    // Expose a Flow of all trips for the user
    val trips: StateFlow<List<Trip>> = tripRepository
        .getAllTripsForUserFlow(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}