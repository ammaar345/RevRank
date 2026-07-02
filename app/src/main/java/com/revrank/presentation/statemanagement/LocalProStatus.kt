package com.revrank.presentation.statemanagement

import com.revrank.data.local.dao.UserDao
import com.revrank.data.revenuecat.PurchaseManager
import com.revrank.data.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for Pro subscription status.
 * Combines signals from:
 * - RevenueCat (via PurchaseManager.isProFlow) — source of truth for active subscription
 * - Local UserDao — for offline/cache consistency
 */
@Singleton
class LocalProStatus @Inject constructor(
    private val purchaseManager: PurchaseManager,
    private val userDao: UserDao,
    private val userRepository: UserRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _current = MutableStateFlow(false)

    /** Emits true when Pro entitlement is active, false otherwise. */
    val current: StateFlow<Boolean> = _current.asStateFlow()

    init {
        // 1. Listen to RevenueCat for real-time subscription state
        scope.launch {
            purchaseManager.isProFlow.collect { isPro ->
                _current.value = isPro
                // Persist to local DAO for offline consistency
                val uid = userRepository.getCurrentUserUid()
                if (uid != null) {
                    userDao.updateProStatus(uid, isPro)
                }
            }
        }

        // 2. Load initial value from local cache (fast startup)
        scope.launch {
            val uid = userRepository.getCurrentUserUid()
            uid?.let { id ->
                userDao.getUserById(id)
                    .first { it != null }
                    ?.let { user ->
                        // Only use cached value if RevenueCat hasn't emitted yet
                        if (_current.value == false && user.isPro) {
                            _current.value = true
                        }
                    }
            }
        }
    }

    /**
     * Force-set Pro status (used after successful purchase before RevenueCat callback fires).
     */
    fun setProStatus(isPro: Boolean) {
        _current.value = isPro
        scope.launch {
            val uid = userRepository.getCurrentUserUid()
            uid?.let { userDao.updateProStatus(it, isPro) }
        }
    }
}
