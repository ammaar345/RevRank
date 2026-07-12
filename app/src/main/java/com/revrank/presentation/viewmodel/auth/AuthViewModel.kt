package com.revrank.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revrank.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives Google Sign-In: takes the id token from the Google account and
 * exchanges it with Firebase via [AuthRepository]. The UI observes [state]
 * and navigates on [AuthUiState.Success].
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed interface AuthUiState {
        data object Idle : AuthUiState
        data object Loading : AuthUiState
        data object Success : AuthUiState
        data class Error(val message: String) : AuthUiState
    }

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken).fold(
                onSuccess = { _state.value = AuthUiState.Success },
                onFailure = { _state.value = AuthUiState.Error(it.message ?: "Sign-in failed") }
            )
        }
    }

    /** Report a failure raised on the UI side (e.g. cancelled Google picker). */
    fun onError(message: String) {
        _state.value = AuthUiState.Error(message)
    }

    fun reset() {
        _state.value = AuthUiState.Idle
    }
}
