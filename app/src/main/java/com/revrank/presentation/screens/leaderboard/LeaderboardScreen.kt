package com.revrank.presentation.screens.leaderboard

import android.graphics.Color.parseColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.domain.model.LeaderboardEntry
import com.revrank.presentation.components.TerminalScaffold
import com.revrank.presentation.theme.*
import com.revrank.presentation.viewmodel.LeaderboardUiState
import com.revrank.presentation.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit = {},
    onUpgradeToPro: () -> Unit = {},
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    TerminalScaffold(screenId = "LEADERBOARD") {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(top = 28.dp)) {

            // ─── HEADER ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LEADERBOARD",
                    fontFamily = ShareTechMono,
                    fontSize = 20.sp,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
            }

            // ─── TABS ───
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Void,
                contentColor = MatrixGreen,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        color = MatrixGreen,
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "GLOBAL",
                            fontFamily = Rajdhani,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (selectedTab == 0) MatrixGreen else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "FRIENDS",
                            fontFamily = Rajdhani,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (selectedTab == 1) MatrixGreen else TextSecondary
                        )
                    }
                )
            }

            // ─── CONTENT ───
            when (selectedTab) {
                0 -> GlobalTab(entries = uiState.globalEntries, isLoading = uiState.isLoading)
                1 -> FriendsTab(
                    entries = uiState.friendsEntries,
                    isLoading = uiState.isLoading,
                    isPro = uiState.isPro,
                    onUpgradeToPro = onUpgradeToPro
                )
            }
        }
    }
}

@Composable
private fun GlobalTab(entries: List<LeaderboardEntry>, isLoading: Boolean) {
    if (isLoading && entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MatrixGreen, strokeWidth = 2.dp)
        }
        return
    }
    LeaderboardList(entries = entries)
}

@Composable
private fun FriendsTab(
    entries: List<LeaderboardEntry>,
    isLoading: Boolean,
    isPro: Boolean,
    onUpgradeToPro: () -> Unit
) {
    if (!isPro) {
        // Soft paywall for free users
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "FRIENDS LEADERBOARD",
                    fontFamily = ShareTechMono,
                    fontSize = 18.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Upgrade to Pro to compete with friends and see who's on top.",
                    fontFamily = Rajdhani,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onUpgradeToPro,
                    colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("UPGRADE TO PRO", fontFamily = Rajdhani, fontWeight = FontWeight.SemiBold, color = Color.Black)
                }
            }
        }
        return
    }

    if (isLoading && entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MatrixGreen, strokeWidth = 2.dp)
        }
        return
    }
    LeaderboardList(entries = entries)
}

@Composable
private fun LeaderboardList(entries: List<LeaderboardEntry>) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(entries, key = { it.uid }) { entry ->
            LeaderboardRow(entry = entry)
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val rankColor = try {
        Color(parseColor(entry.rankColorHex))
    } catch (e: IllegalArgumentException) {
        MatrixGreen
    }
    val animatedColor by animateColorAsState(
        targetValue = rankColor,
        animationSpec = tween(300),
        label = "rankColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Surface, RoundedCornerShape(0.dp))
            .drawBehind {
                // Left green stripe
                drawLine(
                    color = animatedColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 4.dp.toPx()
                )
            }
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text = entry.rank.toString().padStart(2, '0'),
            fontFamily = ShareTechMono,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.width(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Username + rank name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.username,
                fontFamily = Rajdhani,
                fontSize = 16.sp,
                color = TextPrimary
            )
            if (entry.rankName.isNotBlank()) {
                Text(
                    text = entry.rankName.uppercase(),
                    fontFamily = ShareTechMono,
                    fontSize = 10.sp,
                    color = TextDim
                )
            }
        }

        // Avg score (big number)
        Text(
            text = entry.avgScore.toString(),
            fontFamily = ShareTechMono,
            fontSize = 20.sp,
            color = animatedColor
        )
    }
}
