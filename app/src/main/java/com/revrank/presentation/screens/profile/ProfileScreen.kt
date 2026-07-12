package com.revrank.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.domain.model.Rank
import com.revrank.presentation.components.RevRankButton
import com.revrank.presentation.components.TerminalScaffold
import com.revrank.presentation.theme.JetBrainsMono
import com.revrank.presentation.theme.Orbitron
import com.revrank.presentation.theme.Phosphor
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.viewmodel.home.HomeViewModel

@Composable
fun ProfileScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSignOut: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    TerminalScaffold(screenId = "PROFILE") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Rank badge tile
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(state.rank.color.copy(alpha = 0.15f))
                    .border(2.dp, state.rank.color, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.rank.displayName.take(1).uppercase(),
                    fontFamily = Orbitron,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    color = state.rank.color
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = state.username,
                fontFamily = Orbitron,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 2.sp,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${state.rank.displayName.uppercase()} · RANK ${state.rank.ordinal + 1}/7",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = state.rank.color
            )

            Spacer(Modifier.height(28.dp))

            // Stats grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatTile("TRIPS", state.weeklyTrips.toString(), Modifier.weight(1f))
                StatTile("KM", "%.0f".format(state.weeklyKm), Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatTile("XP", state.xp.toString(), Modifier.weight(1f))
                StatTile("STREAK", "${state.streakDays}d", Modifier.weight(1f))
            }

            Spacer(Modifier.height(28.dp))

            // Settings rows
            SectionLabel("SETTINGS")
            SettingRow("Notifications")
            SettingRow("Units · Metric (km)")
            SettingRow("Privacy & Data")
            SettingRow("Terms & Conditions")

            Spacer(Modifier.height(28.dp))
            RevRankButton(
                text = "Sign out",
                onClick = onSignOut
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(Phosphor.copy(alpha = 0.05f))
            .border(1.dp, Phosphor.copy(alpha = 0.25f), RoundedCornerShape(2.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Phosphor
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = JetBrainsMono,
        fontSize = 10.sp,
        letterSpacing = 3.sp,
        color = TextSecondary,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
private fun SettingRow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = Rajdhani,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(text = "›", fontFamily = JetBrainsMono, fontSize = 18.sp, color = TextSecondary)
    }
}
