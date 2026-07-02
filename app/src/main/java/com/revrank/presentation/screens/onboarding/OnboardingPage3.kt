package com.revrank.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.Border
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.MatrixGlow
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ScoreGood
import com.revrank.presentation.theme.Surface
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.RevRankTypography
import com.revrank.presentation.theme.Void

@Composable
fun OnboardingPage3() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHOOSE YOUR PATH",
            style = RevRankTypography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FREE FOREVER",
                    style = RevRankTypography.labelSmall,
                    color = MatrixGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                FreeFeatureItem("Unlimited trips")
                FreeFeatureItem("Trip scoring")
                FreeFeatureItem("7 rank tiers")
                FreeFeatureItem("10-trip history")
                FreeFeatureItem("Badges & achievements")
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PRO UNLOCKS",
                    style = RevRankTypography.labelSmall,
                    color = ScoreGood
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProFeatureItem("Unlimited history")
                ProFeatureItem("Route replay")
                ProFeatureItem("G-force data")
                ProFeatureItem("Friends leaderboard")
                ProFeatureItem("Export CSV")
            }
        }
    }
}

@Composable
private fun FreeFeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓",
            color = MatrixGreen,
            style = RevRankTypography.bodyMedium
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            color = TextPrimary,
            style = RevRankTypography.bodyMedium
        )
    }
}

@Composable
private fun ProFeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\uD83D\uDD12", // Lock emoji
            color = ScoreGood,
            style = RevRankTypography.bodyMedium
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            color = TextSecondary,
            style = RevRankTypography.bodyMedium
        )
    }
}
