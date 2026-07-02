package com.revrank.domain.usecase

import com.revrank.data.repository.TripRepository
import javax.inject.Inject

class SyncTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke() {
        tripRepository.syncUnsyncedTrips()
    }
}
