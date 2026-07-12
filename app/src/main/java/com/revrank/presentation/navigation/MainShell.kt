package com.revrank.presentation.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.presentation.components.RevRankIcons
import com.revrank.service.TripTrackingService
import com.revrank.presentation.screens.home.HomeScreen
import com.revrank.presentation.screens.profile.ProfileScreen
import com.revrank.presentation.screens.ranks.RanksScreen
import com.revrank.presentation.screens.trips.TripsScreen
import com.revrank.presentation.theme.Phosphor
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.theme.Void
import com.revrank.presentation.viewmodel.home.HomeViewModel

private data class Tab(val label: String, val icon: ImageVector)

/**
 * Primary in-app shell: the 4 top-level tabs with the CRT terminal bottom nav.
 * Detail screens (paywall, g-force, route replay, leaderboard…) are still pushed
 * onto the outer nav graph via the [onNavigate] callbacks.
 */
@Composable
fun MainShell(
    onOpenTrip: (String) -> Unit = {},
    onUpgrade: () -> Unit = {},
    onOpenLeaderboard: () -> Unit = {}
) {
    val tabs = remember {
        listOf(
            Tab("TRACK", RevRankIcons.Radar),
            Tab("HISTORY", RevRankIcons.History),
            Tab("RANK", RevRankIcons.Trophy),
            Tab("PROFILE", RevRankIcons.User)
        )
    }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        containerColor = Void,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF050505)) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontFamily = ShareTechMono,
                                fontSize = 8.sp,
                                letterSpacing = 1.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Phosphor,
                            selectedTextColor = Phosphor,
                            unselectedIconColor = Color(0xFF555555),
                            unselectedTextColor = Color(0xFF555555),
                            indicatorColor = Color(0x1A00FF66)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selected) {
                0 -> {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val uiState by homeViewModel.uiState.collectAsState()
                    HomeScreen(
                        username = uiState.username,
                        xp = uiState.xp,
                        rank = uiState.rank,
                        streakDays = uiState.streakDays,
                        weeklyTrips = uiState.weeklyTrips,
                        weeklyKm = uiState.weeklyKm,
                        challenges = uiState.challenges,
                        lastTrip = uiState.lastTrip,
                        onStartTrip = {
                            val startIntent = Intent(context, TripTrackingService::class.java).apply {
                                action = TripTrackingService.ACTION_START
                            }
                            ContextCompat.startForegroundService(context, startIntent)
                        },
                        onViewRanks = { selected = 2 },
                        onViewTrip = onOpenTrip
                    )
                }
                1 -> TripsScreen(onTripClick = onOpenTrip, onUpgradeClick = onUpgrade)
                2 -> {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val uiState by homeViewModel.uiState.collectAsState()
                    RanksScreen(
                        currentRank = uiState.rank,
                        xp = uiState.xp,
                        badges = emptyList()
                    )
                }
                3 -> ProfileScreen()
            }
        }
    }
}
