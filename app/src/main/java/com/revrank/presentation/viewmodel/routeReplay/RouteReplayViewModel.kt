package com.revrank.presentation.viewmodel.routeReplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelInitializer
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@HiltViewModel
class RouteReplayViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    val tripId: String
    private val _trip = androidx.compose.runtime.MutableStateFlow<Trip?>(null)
    val trip: androidx.compose.runtime.StateFlow<Trip?> = _trip.asStateFlow()

    init {
        // In a real implementation, we would get the tripId from navigation arguments
        // For now, we'll assume it's set via a setter or we'll load a default for preview
        // This is a placeholder - actual implementation would get from SavedStateHandle
        tripId = "placeholder_trip_id"
        loadTrip()
    }

    private fun loadTrip() {
        viewModelScope.launch {
            tripRepository.getAllTripsForUserFlow("current_user_id") // TODO: get actual userId
                .map { list ->
                    list.firstOrNull { it.id == tripId }
                }
                .collect { trip ->
                    _trip.value = trip
                }
        }
    }

    // Factory for Hilt
    companion object {
        val Factory: (tripId: String) -> (() => RouteReplayViewModel) = { tripId ->
            {
                // This is a simplified factory - in reality, Hilt would handle injection
                // We'd need to pass the tripId via SavedStateHandle or similar
                throw UnsupportedOperationException("Use Hilt with AssistedInject for factory with parameters")
            }
        }
    }
}