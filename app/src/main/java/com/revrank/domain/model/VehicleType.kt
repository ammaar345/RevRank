package com.revrank.domain.model

/**
 * Vehicle category chosen during onboarding. Lives in the domain layer so
 * Trip and sync models can reference it without depending on presentation.
 */
enum class VehicleType {
    CAR, MOTORCYCLE, VAN, TRUCK
}
