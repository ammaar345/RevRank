package com.revrank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _activeTrip = tripRepository.getActiveTripFlow(/* userId */ "current_user") // In real app, get from auth
        .stateIn(
            scope = viewModelScope,
            started = androidx.lifecycle.SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val activeTrip: StateFlow<Trip?> = _activeTrip

    // Expose a Flow for whether a trip is active (non-null)
    val isTripActive: StateFlow<Boolean> = activeTrip.map { it != null }.stateIn(
        scope = viewModelScope,
        started = androidx.lifecycle.SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
}