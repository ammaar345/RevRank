package com.revrank.presentation.screens.trips

import androidx.compose.foundation.Image
import androidx.compose.foundation.LazyColumn
import androidx.compose.foundation.ItemScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.revrank.domain.model.Trip
import com.revrank.presentation.theme.Color
import com.revrank.presentation.theme.Type
import com.revrank.presentation.viewmodel.trips.TripsViewModel
import com.revrank.presentation.statemanagement.LocalProStatus
import com.revrank.presentation.components.ProGate
import androidx.hilt.navigation.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import com.revrank.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    viewModel: TripsViewModel = hiltViewModel(),
    proStatus: LocalProStatus = LocalProStatusImpl()
) {
    val trips by viewModel.trips.collectAsStateWithLifecycle()
    val isPro by proStatus.current.collectAsStateWithLifecycle()

    // Determine which trips to show based on Pro status
    val displayedTrips = if (isPro) {
        // Pro: show all trips, grouped by month (we'll implement grouping later)
        trips
    } else {
        // Free: show only last 30 days, max 10 trips
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
        trips
            .filter { it.startTime >= thirtyDaysAgo }
            .take(10)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Trips",
                        style = Type.TitleLarge,
                        color = Color.White
                    )
                },
                backgroundColor = Color(0xFF000000)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (trips.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_directions_car),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .alpha(0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No trips yet",
                        style = Type.TitleMedium,
                        color = Color(0xFF888888)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Go for a drive to start tracking your trips",
                        style = Type.LabelLarge,
                        color = Color(0xFF444444)
                    )
                }
            } else {
                // Show trips list
                if (!isPro && displayedTrips.size < trips.size) {
                    // Show soft gate for free users
                    ProGate(
                        content = {
                            TripsList(trips = displayedTrips, isPro = true)
                        },
                        fallback = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Unlimited Trip History",
                                    style = Type.TitleMedium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Upgrade to Pro to see all your trips with monthly grouping",
                                    style = Type.LabelLarge,
                                    color = Color(0xFF888888)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                // Show limited trips anyway
                                TripsList(trips = displayedTrips, isPro = false)
                            }
                        },
                        proStatus = proStatus
                    )
                } else {
                    TripsList(trips = displayedTrips, isPro = isPro)
                }
            }
        }
    }
}

@Composable
private fun TripsList(trips: List<Trip>, isPro: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tripList = trips) { trip ->
            TripItem(trip = trip, isPro = isPro)
        }
        
        // Show footer for free users indicating limit
        if (!isPro && trips.size < 10) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Showing ${trips.size} of ${trips.size} recent trips (last 30 days)",
                    style = Type.LabelSmall,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center
                )
            }
        } else if (!isPro && trips.size >= 10) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Showing 10 most recent trips (last 30 days)",
                    style = Type.LabelSmall,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Upgrade to Pro for unlimited history",
                    style = Type.LabelSmall,
                    color = Color(0xFFFF2D00),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TripItem(trip: Trip, isPro: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF0A0A0A))
            .clickable { /* TODO: Navigate to trip detail */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trip info
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${trip.distanceKm.formatDistance()}km",
                    style = Type.TitleLarge,
                    color = Color.White,
                    fontFamily = "Share Tech Mono"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    style = Type.TitleLarge,
                    color = Color(0xFF444444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${trip.durationMin}min",
                    style = Type.TitleLarge,
                    color = Color.White,
                    fontFamily = "Share Tech Mono"
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = DateUtils.formatDate(trip.startTime),
                    style = Type.LabelLarge,
                    color = Color(0xFF888888)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    style = Type.LabelLarge,
                    color = Color(0xFF444444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = trip.score.toString(),
                    style = Type.TitleLarge,
                    color = getScoreColor(trip.score),
                    fontFamily = "Share Tech Mono"
                )
            }
        }
        
        // Chevron indicator
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Next",
            tint = Color(0xFF444444),
            modifier = Modifier
                .size(24.dp)
        )
    }
}

private fun ScoreColor(score: Int): Color {
    return when {
        score >= 90 -> Color(0xFF00FF41) // Green
        score >= 75 -> Color(0xFF39FF14) // Lime
        score >= 55 -> Color(0xFFFFD700) // Amber
        score >= 35 -> Color(0xFFFF6B00) // Orange
        else -> Color(0xFFFF2D00)        // Red
    }
}

// Extension to format distance
private fun Double.formatDistance(): String {
    return if (this >= 1) {
        "%.1f".format(this)
    } else {
        "${(this * 1000).toInt()}m"
    }
}

// Extension to format duration
private fun Long.durationMin(): String {
    val minutes = this / 60
    return if (minutes >= 60) {
        "${minutes / 60}h ${minutes % 60}min"
    } else {
        "$minutes min"
    }
}