package com.revrank.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.revrank.presentation.statemanagement.LocalProStatus
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Gate for Pro features. Shows content if user is Pro, otherwise shows fallback.
 *
 * @param content Composable to show when user has Pro access
 * @param fallback Composable to show when user does NOT have Pro access (defaults to ProUpsellBanner)
 * @param proStatus Injected via Hilt EntryPoint by default, can be overridden for preview/testing
 */
@Composable
fun ProGate(
    content: @Composable () -> Unit,
    fallback: @Composable () -> Unit = {
        com.revrank.presentation.screens.paywall.ProUpsellBanner()
    },
    proStatus: LocalProStatus? = null
) {
    val resolvedProStatus = remember(proStatus) {
        proStatus ?: run {
            val context = LocalContext.current.applicationContext
            val entryPoint = EntryPointAccessors.fromApplication(
                context,
                ProGateEntryPoint::class.java
            )
            entryPoint.proStatus()
        }
    }
    val isPro by resolvedProStatus.current.collectAsState(initial = false)
    if (isPro) content() else fallback()
}

/** Hilt entry point for accessing LocalProStatus from a non-Hilt composable. */
@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface ProGateEntryPoint {
    fun proStatus(): LocalProStatus
}
