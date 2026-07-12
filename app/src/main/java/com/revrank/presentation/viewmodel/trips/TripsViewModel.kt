package com.revrank.presentation.viewmodel.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.SessionManager
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    session: SessionManager
) : ViewModel() {

    // Expose a Flow of all trips for the signed-in user
    val trips: StateFlow<List<Trip>> = tripRepository
        .getAllTripsForUserFlow(session.currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}