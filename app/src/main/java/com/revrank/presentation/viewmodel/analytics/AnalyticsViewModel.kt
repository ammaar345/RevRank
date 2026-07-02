package com.revrank.presentation.viewmodel.analytics

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
import androidx.lifecycle.viewModel.
We 
com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    // We can expose flows for the data needed by the charts
    // For example: a flow of trips for the last 30 days, or grouped by month, etc.

    // Placeholder: we can expose a simple flow of all trips (for now)
    val trips: androidx.lifecycle.StateFlow<List<Trip>> = 
        tripRepository.getAllTripsForUserFlow("current_user_id") // TODO: get actual userId
            .map { list -> list }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // We can also add methods to compute aggregated data for the charts, but for now we leave it as is.
}