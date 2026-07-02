package com.revrank.presentation.viewmodel.gforce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class GForceViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _trip = androidx.compose.runtime.MutableStateFlow<Trip?>(null)
    val trip: androidx.compose.runtime.StateFlow<Trip?> = _trip.asStateFlow()

    init {
        // Get the tripId from the SavedStateHandle (which comes from navigation arguments)
        val tripId = savedStateHandle.get<String>("tripId") ?: return
        loadTrip(tripId)
    }

    private fun loadTrip(tripId: String) {
        viewModelScope.launch {
            // In a real app, we would get the current user ID from auth
            // For now, we'll use a placeholder - this should be replaced with actual user ID
            val userId = "current_user_id" 
            tripRepository.getAllTripsForUserFlow(userId)
                .map { list ->
                    list.firstOrNull { it.id == tripId }
                }
                .collect { trip ->
                    _trip.value = trip
                }
        }
    }
}