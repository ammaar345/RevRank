package com.revrank.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.revrank.domain.model.VehicleType

class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable: StateFlow<Boolean?> = _usernameAvailable

    private val _vehicleType = MutableStateFlow<VehicleType?>(null)
    val vehicleType: StateFlow<VehicleType?> = _vehicleType

    private val _locationGranted = MutableStateFlow(false)
    val locationGranted: StateFlow<Boolean> = _locationGranted

    private val _motionGranted = MutableStateFlow(false)
    val motionGranted: StateFlow<Boolean> = _motionGranted

    private val _onboardingComplete = MutableStateFlow(false)
    val onboardingComplete: StateFlow<Boolean> = _onboardingComplete

    fun setPage(page: Int) {
        _currentPage.value = page
    }

    fun checkUsername(name: String) {
        _username.value = name
        // In production, this would call AuthRepository to check Firestore
        viewModelScope.launch {
            kotlinx.coroutines.delay(500) // Debounce simulation
            // Placeholder: assume available if not "taken"
            _usernameAvailable.value = name.lowercase() != "taken"
        }
    }

    fun setVehicleType(type: VehicleType) {
        _vehicleType.value = type
    }

    fun setLocationGranted(granted: Boolean) {
        _locationGranted.value = granted
    }

    fun setMotionGranted(granted: Boolean) {
        _motionGranted.value = granted
    }

    fun completeOnboarding() {
        _onboardingComplete.value = true
        // Persist to DataStore in production
    }
}
