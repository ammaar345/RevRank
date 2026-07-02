package com.revrank.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Trophy
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.MatrixMuted
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.TextDim

@Composable
fun EmptyState(
    icon: ImageVector,
    headline: String,
    subtext: String,
    cta: Pair<String, () -> Unit>? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = headline,
            tint = MatrixMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDim,
            textAlign = TextAlign.Center
        )
        if (cta != null) {
            Spacer(Modifier.height(24.dp))
            RevRankButton(
                text = cta.first,
                onClick = cta.second
            )
        }
    }
}

object EmptyStates {
    val noTrips = Triple(
        Icons.Filled.Speed,
        "NO TRIPS YET",
        "Take your first drive to get your score."
    )
    val noBadges = Triple(
        Icons.Filled.Trophy,
        "NO BADGES YET",
        "Complete trips to earn your first badge."
    )
    val noFriends = Triple(
        Icons.Filled.PersonAdd,
        "NO FRIENDS YET",
        "Challenge someone — share your trip card."
    )
    val leaderboardFailed = Triple(
        Icons.Filled.WifiOff,
        "COULDN'T LOAD",
        "Check your connection."
    )
}
