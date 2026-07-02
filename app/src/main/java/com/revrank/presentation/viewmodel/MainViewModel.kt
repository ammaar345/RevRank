package com.revrank.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() {

    private val _pendingDeepLink = MutableStateFlow<Uri?>(null)
    val pendingDeepLink: StateFlow<Uri?> = _pendingDeepLink.asStateFlow()

    fun setPendingDeepLink(uri: Uri?) {
        _pendingDeepLink.value = uri
    }

    /** Call after handling the link to clear it */
    fun clearPendingDeepLink() {
        _pendingDeepLink.value = null
    }
}