package com.revrank.data.revenuecat

import android.app.Activity
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

sealed class PurchaseResult {
    data object Success : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
    data object Cancelled : PurchaseResult()
}

@Singleton
class PurchaseManager @Inject constructor() {

    /** Emits true when Pro entitlement is active, false otherwise. */
    val isProFlow: Flow<Boolean> = callbackFlow {
        val listener = UpdatedCustomerInfoListener { info: CustomerInfo ->
            trySend(info.entitlements["pro"]?.isActive == true)
        }
        Purchases.sharedInstance.updatedCustomerInfoListener = listener
        // Emit current state immediately
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = { trySend(false) },
            onSuccess = { info -> trySend(info.entitlements["pro"]?.isActive == true) }
        )
        awaitClose {
            Purchases.sharedInstance.updatedCustomerInfoListener = null
        }
    }.distinctUntilChanged()

    /** Fetches available offerings (monthly + annual). */
    suspend fun getOfferings(): Offerings? = suspendCancellableCoroutine { cont ->
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                if (cont.isActive) cont.resume(null) { }
            },
            onSuccess = { offerings ->
                if (cont.isActive) cont.resume(offerings) { }
            }
        )
    }

    /** Purchases a package. Returns Success, Cancelled, or Error. */
    suspend fun purchasePackage(
        activity: Activity,
        packageToPurchase: Package
    ): PurchaseResult = suspendCancellableCoroutine { cont ->
        Purchases.sharedInstance.purchaseWith(
            purchaseParams = PurchaseParams.Builder(activity, packageToPurchase).build(),
            onError = { error: PurchasesError, userCancelled: Boolean ->
                if (cont.isActive) {
                    val result = if (userCancelled) {
                        PurchaseResult.Cancelled
                    } else {
                        PurchaseResult.Error(error.message)
                    }
                    cont.resume(result) { }
                }
            },
            onSuccess = { _: StoreTransaction?, _: CustomerInfo ->
                if (cont.isActive) cont.resume(PurchaseResult.Success) { }
            }
        )
    }

    /** Restores purchases. Returns true if Pro entitlement is now active. */
    suspend fun restorePurchases(): Boolean = suspendCancellableCoroutine { cont ->
        Purchases.sharedInstance.restorePurchasesWith(
            onError = { _: PurchasesError ->
                if (cont.isActive) cont.resume(false) { }
            },
            onSuccess = { info: CustomerInfo ->
                val isPro = info.entitlements["pro"]?.isActive == true
                if (cont.isActive) cont.resume(isPro) { }
            }
        )
    }

    /** Fetches the currently active Pro package (annual preferred, fallback monthly). */
    suspend fun getProPackage(): Package? {
        val offerings = getOfferings() ?: return null
        val current = offerings.current ?: return null
        // Prefer annual, fall back to monthly
        return current.annual ?: current.monthly
    }

    /** Returns months of Pro credit for a referral grant. */
    fun getReferralGrantMonths(): Int = 1 // 30 days
}
