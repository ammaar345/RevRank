package com.revrank.presentation.viewmodel.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.TripRepository
import com.revrank.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    tripRepository: TripRepository
) : ViewModel() {

    // All trips for the charts. TODO: wire real userId from auth.
    val trips: StateFlow<List<Trip>> =
        tripRepository.getAllTripsForUserFlow("current_user_id")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
