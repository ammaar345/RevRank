package com.revrank.presentation.screens.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.domain.model.Trip
import com.revrank.presentation.components.EmptyState
import com.revrank.presentation.components.EmptyStates
import com.revrank.presentation.components.ProGate
import com.revrank.presentation.components.TerminalScaffold
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.viewmodel.trips.TripsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FREE_HISTORY_DAYS = 30L
private const val FREE_HISTORY_MAX = 10

/**
 * Trip history list. Free users see the last 30 days (max 10 trips);
 * Pro users get unlimited history.
 */
@Composable
fun TripsScreen(
    viewModel: TripsViewModel = hiltViewModel(),
    onTripClick: (String) -> Unit = {},
    onUpgradeClick: () -> Unit = {}
) {
    val trips by viewModel.trips.collectAsState()

    TerminalScaffold(screenId = "HISTORY") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Text(
                text = "TRIPS",
                fontFamily = ShareTechMono,
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 16.dp)
            )

            // if/else — no early return from the composable, which corrupts
            // Compose's group stack when the trips flow flips empty -> non-empty.
            if (trips.isEmpty()) {
                EmptyState(
                    icon = EmptyStates.noTrips.first,
                    headline = EmptyStates.noTrips.second,
                    subtext = EmptyStates.noTrips.third
                )
            } else {
                ProGate(
                    content = { TripsList(trips = trips, isPro = true, onTripClick = onTripClick) },
                    fallback = {
                        val thirtyDaysAgo =
                            System.currentTimeMillis() - FREE_HISTORY_DAYS * 24 * 60 * 60 * 1000
                        val limited = trips
                            .filter { it.startTime >= thirtyDaysAgo }
                            .take(FREE_HISTORY_MAX)
                        TripsList(
                            trips = limited,
                            isPro = false,
                            onTripClick = onTripClick,
                            footer = {
                                FreeLimitFooter(
                                    shown = limited.size,
                                    total = trips.size,
                                    onUpgradeClick = onUpgradeClick
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TripsList(
    trips: List<Trip>,
    isPro: Boolean,
    onTripClick: (String) -> Unit,
    footer: (@Composable () -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp, vertical = 8.dp
        )
    ) {
        items(trips, key = { it.id }) { trip ->
            TripItem(trip = trip, onClick = { onTripClick(trip.id) })
        }
        if (footer != null) {
            item { footer() }
        }
    }
}

@Composable
private fun FreeLimitFooter(shown: Int, total: Int, onUpgradeClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Showing $shown of $total trips (last $FREE_HISTORY_DAYS days)",
            fontFamily = Rajdhani,
            fontSize = 12.sp,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "UPGRADE TO PRO FOR UNLIMITED HISTORY",
            fontFamily = ShareTechMono,
            fontSize = 12.sp,
            color = MatrixGreen,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { onUpgradeClick() }
        )
    }
}

@Composable
private fun TripItem(trip: Trip, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = formatDistance(trip.distanceKm),
                    fontFamily = ShareTechMono,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "·",
                    fontFamily = ShareTechMono,
                    fontSize = 16.sp,
                    color = Color(0xFF444444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDuration(trip),
                    fontFamily = ShareTechMono,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(trip.startTime),
                fontFamily = Rajdhani,
                fontSize = 12.sp,
                color = Color(0xFF888888)
            )
        }
        Text(
            text = trip.score.toString(),
            fontFamily = ShareTechMono,
            fontSize = 22.sp,
            color = scoreColor(trip.score)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open trip",
            tint = Color(0xFF444444),
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun scoreColor(score: Int): Color = when {
    score >= 90 -> Color(0xFF00FF41)
    score >= 75 -> Color(0xFFADFF2F)
    score >= 55 -> Color(0xFFFFD700)
    score >= 35 -> Color(0xFFFF6B00)
    else -> Color(0xFFFF453A)
}

private fun formatDistance(km: Double): String =
    if (km >= 1) "%.1f km".format(km) else "${(km * 1000).toInt()} m"

private fun formatDuration(trip: Trip): String {
    val minutes = ((trip.endTime ?: trip.startTime) - trip.startTime) / 1000 / 60
    return if (minutes >= 60) "${minutes / 60}h ${minutes % 60}min" else "${minutes}min"
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("MMM d · HH:mm", Locale.getDefault()).format(Date(timestamp))
