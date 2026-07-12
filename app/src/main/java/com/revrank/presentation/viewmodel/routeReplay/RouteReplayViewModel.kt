package com.revrank.presentation.viewmodel.routeReplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.SessionManager
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class RouteReplayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val session: SessionManager
) : ViewModel() {

    val tripId: String = savedStateHandle["tripId"] ?: ""

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    init {
        loadTrip()
    }

    private fun loadTrip() {
        viewModelScope.launch {
            tripRepository.getAllTripsForUserFlow(session.currentUserId)
                .map { list -> list.firstOrNull { it.id == tripId } }
                .collect { _trip.value = it }
        }
    }
}
