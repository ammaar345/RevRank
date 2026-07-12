package com.revrank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.SessionManager
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    session: SessionManager
) : ViewModel() {

    private val userId = session.currentUserId

    private val _activeTrip = tripRepository.getActiveTripFlow(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val activeTrip: StateFlow<Trip?> = _activeTrip

    // Expose a Flow for whether a trip is active (non-null)
    val isTripActive: StateFlow<Boolean> = activeTrip.map { it != null }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Id of the trip the user just ended. We then look the trip up reactively and
    // only surface it once it has been FINALIZED (endTime set), so the score
    // reveal shows the real final score / distance / duration rather than the
    // pre-finalize snapshot.
    private val _endingTripId = MutableStateFlow<String?>(null)

    val justEndedTrip: StateFlow<Trip?> = _endingTripId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            tripRepository.getAllTripsForUserFlow(userId).map { trips ->
                trips.firstOrNull { it.id == id && it.endTime != null }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    /** Call when the user taps End Trip, while the active trip is still present. */
    fun captureEndingTrip() {
        _endingTripId.value = activeTrip.value?.id
    }

    /** Call after the score reveal is dismissed. */
    fun clearEndedTrip() {
        _endingTripId.value = null
    }
}
