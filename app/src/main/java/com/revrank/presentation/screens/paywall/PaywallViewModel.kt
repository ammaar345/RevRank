package com.revrank.presentation.screens.paywall

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revrank.data.revenuecat.PurchaseManager
import com.revrank.data.revenuecat.PurchaseResult
import com.revrank.domain.model.PricingPeriod
import com.revrank.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaywallUiState(
    val isLoading: Boolean = true,
    val offerings: Offerings? = null,
    val selectedPeriod: PricingPeriod = PricingPeriod.ANNUAL,
    val isPurchasing: Boolean = false,
    val purchaseResult: PurchaseResult? = null,
    val error: String? = null,
    val isFirstEncounter: Boolean = true
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val purchaseManager: PurchaseManager,
    application: Application
) : ViewModel() {

    private val context = application.applicationContext

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    init {
        loadOfferings()
        _uiState.update {
            it.copy(isFirstEncounter = !PreferenceUtil.hasSeenPaywall)
        }
    }

    private fun loadOfferings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val offerings = purchaseManager.getOfferings()
                _uiState.update { it.copy(isLoading = false, offerings = offerings) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load pricing. Check your connection.")
                }
            }
        }
    }

    fun selectPeriod(period: PricingPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    /** Returns the Package for selected period from current offerings. */
    private fun getSelectedPackage(): Package? {
        val state = _uiState.value
        val current = state.offerings?.current ?: return null
        return when (state.selectedPeriod) {
            PricingPeriod.ANNUAL -> current.annual ?: current.monthly
            PricingPeriod.MONTHLY -> current.monthly ?: current.annual
        }
    }

    fun purchase(activity: android.app.Activity) {
        val pkg = getSelectedPackage() ?: return
        _uiState.update { it.copy(isPurchasing = true, error = null) }

        // Mark first encounter seen
        PreferenceUtil.hasSeenPaywall = true

        viewModelScope.launch {
            val result = purchaseManager.purchasePackage(activity, pkg)
            _uiState.update {
                it.copy(
                    isPurchasing = false,
                    purchaseResult = result,
                    error = if (result is PurchaseResult.Error) result.message else null
                )
            }
        }
    }

    fun restore() {
        _uiState.update { it.copy(isPurchasing = true, error = null) }
        viewModelScope.launch {
            val restored = purchaseManager.restorePurchases()
            _uiState.update {
                it.copy(
                    isPurchasing = false,
                    purchaseResult = if (restored) PurchaseResult.Success else {
                        PurchaseResult.Error("No previous purchases found to restore.")
                    }
                )
            }
        }
    }

    fun dismissResult() {
        _uiState.update { it.copy(purchaseResult = null) }
    }

    fun retry() {
        loadOfferings()
    }

    /** Returns pricing display strings based on offerings. */
    fun getPricingStrings(): PricingStrings? {
        val current = _uiState.value.offerings?.current ?: return null
        val monthly = current.monthly
        val annual = current.annual

        return PricingStrings(
            monthlyLabel = monthly?.storeProduct?.priceFormatted ?: "$3.99/mo",
            monthlyPrice = monthly?.storeProduct?.price ?: 3.99,
            annualLabel = annual?.storeProduct?.priceFormatted ?: "$24.99/yr",
            annualPrice = annual?.storeProduct?.price ?: 24.99,
            annualSavings = calculateSavings(
                monthlyPrice = monthly?.storeProduct?.price ?: 3.99,
                annualPrice = annual?.storeProduct?.price ?: 24.99
            )
        )
    }

    private fun calculateSavings(monthlyPrice: Double, annualPrice: Double): Int {
        val yearlyMonthly = monthlyPrice * 12
        if (yearlyMonthly <= 0) return 0
        return ((yearlyMonthly - annualPrice) / yearlyMonthly * 100).toInt()
    }
}

data class PricingStrings(
    val monthlyLabel: String,
    val monthlyPrice: Double,
    val annualLabel: String,
    val annualPrice: Double,
    val annualSavings: Int
)
