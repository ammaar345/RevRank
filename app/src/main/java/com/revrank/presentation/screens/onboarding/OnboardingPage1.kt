package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.revrank.presentation.theme.RevRankTypography
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.Void
import com.revrank.presentation.theme.TextPrimary

@Composable
fun OnboardingPage1() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie placeholder - in real app this would be an animated speedometer
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "[Speedometer Animation]",
                style = RevRankTypography.bodyMedium,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "YOUR DRIVES, RANKED.",
            style = RevRankTypography.displaySmall,
            color = MatrixGreen
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Track every trip. Get a score. Climb the ranks. \nAll from your phone.",
            style = RevRankTypography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
