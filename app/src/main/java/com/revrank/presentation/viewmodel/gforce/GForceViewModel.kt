package com.revrank.presentation.viewmodel.gforce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class GForceViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    init {
        // tripId comes from navigation arguments via the SavedStateHandle
        savedStateHandle.get<String>("tripId")?.let { loadTrip(it) }
    }

    private fun loadTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.getAllTripsForUserFlow("current_user_id") // TODO: wire real userId from auth
                .map { list -> list.firstOrNull { it.id == tripId } }
                .collect { _trip.value = it }
        }
    }
}
