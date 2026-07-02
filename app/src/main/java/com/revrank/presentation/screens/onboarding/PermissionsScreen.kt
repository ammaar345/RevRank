package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revrank.presentation.components.RevRankButton
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Surface
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.RevRankTypography

@Composable
fun PermissionsScreen(
    onGrantLocation: () -> Unit,
    onGrantMotion: () -> Unit,
    locationGranted: Boolean,
    motionGranted: Boolean,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PERMISSIONS",
            style = RevRankTypography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        PermissionRow(
            title = "Location (Always)",
            description = "We need always-on location to auto-detect when you start driving.",
            granted = locationGranted,
            onGrant = onGrantLocation
        )
        Spacer(modifier = Modifier.height(16.dp))
        PermissionRow(
            title = "Motion Sensors",
            description = "We read your accelerometer to score braking and cornering.",
            granted = motionGranted,
            onGrant = onGrantMotion
        )
        Spacer(modifier = Modifier.height(24.dp))
        RevRankButton(
            text = "Continue",
            onClick = onComplete,
            enabled = locationGranted && motionGranted
        )
    }
}

@Composable
private fun PermissionRow(
    title: String,
    description: String,
    granted: Boolean,
    onGrant: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = title, style = RevRankTypography.bodyLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description, style = RevRankTypography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        RevRankButton(
            text = if (granted) "Granted" else "Grant",
            onClick = onGrant,
            enabled = !granted
        )
    }
}
