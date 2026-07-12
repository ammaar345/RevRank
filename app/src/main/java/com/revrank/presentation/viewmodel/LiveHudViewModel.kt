package com.revrank.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.revrank.data.repository.TripTrackingRepository
import com.revrank.domain.model.DrivingQuality
import com.revrank.utils.TripScoreCalculator.ScoreBreakdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Exposes the live trip state held by TripTrackingRepository to the HUD.
 */
@HiltViewModel
class LiveHudViewModel @Inject constructor(
    tripTrackingRepository: TripTrackingRepository
) : ViewModel() {
    val speed: StateFlow<Double> = tripTrackingRepository.currentSpeed
    val score: StateFlow<Int> = tripTrackingRepository.currentScore
    val distanceKm: StateFlow<Double> = tripTrackingRepository.distanceKm
    val drivingQuality: StateFlow<DrivingQuality> = tripTrackingRepository.drivingQuality
    val scoreBreakdown: StateFlow<ScoreBreakdown> = tripTrackingRepository.scoreBreakdown
}
