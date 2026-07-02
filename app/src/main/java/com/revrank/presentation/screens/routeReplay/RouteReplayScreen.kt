package com.revrank.presentation.screens.routeReplay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.revrank.domain.model.Trip
import com.revrank.presentation.components.ProGate
import com.revrank.presentation.screens.paywall.ProUpsellBanner
import com.revrank.presentation.theme.*
import com.revrank.presentation.viewmodel.routeReplay.RouteReplayViewModel
import androidx.hilt.navigation.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Route Replay Screen (Pro feature)
 * Shows a map with the route drawn as a polyline colored by score segments,
 * plus a bottom sheet with playback controls, speed chart, and segment stats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteReplayScreen(
    tripId: String,
    viewModel: RouteReplayViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onUpgradeClick: () -> Unit = {}
) {
    // Pro-gated: show upsell banner if not Pro
    ProGate(
        content = {
            RouteReplayContent(tripId = tripId, viewModel = viewModel, onDismiss = onDismiss)
        },
        fallback = {
            ProUpsellBanner(
                featureName = "route replay",
                featureDescription = "Watch your drive with a live score overlay.",
                onSubscribe = onUpgradeClick,
                onDismiss = onDismiss
            )
        }
    )
}

@Composable
private fun RouteReplayContent(
    tripId: String,
    viewModel: RouteReplayViewModel,
    onDismiss: () -> Unit
) {
    val trip by viewModel.trip.collectAsStateWithLifecycle()
    val isLoading = trip == null

    if (isLoading) {
        // Show loading placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A))
        ) {
            CircularProgressIndicator(
                color = Color(0xFF00FF41),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    // TODO: Implement actual map view with Google Maps Compose or Android View
    // For now, placeholder UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        // Map placeholder (would be Google Maps)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(300.dp)
                .background(Color(0xFF111111))
        ) {
            Text(
                text = "MAP PLACEHOLDER - Route Polyline",
                color = Color(0xFF888888),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom sheet for controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(200.dp)
                .background(Color(0xFF0A0A0A))
                .border(1.dp, Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                ) {
                    Text(
                        text = "Route Replay Controls",
                        style = Type.TitleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { /* TODO: Rewind 10s */ },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFF1E1E1E),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Replay 10 seconds",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { /* TODO: Play/Pause */ },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFF00FF41),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PauseCircleFilled,
                                contentDescription = "Pause",
                                tint = Color.Black
                            )
                        }
                        IconButton(
                            onClick = { /* TODO: Forward 10s */ },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFF1E1E1E),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay30,
                                contentDescription = "Forward 10 seconds",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Scrub bar placeholder
                    Slider(
                        value = 0f,
                        onValueChange = { /* TODO: Seek */ },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF00FF41),
                        thumbColor = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "0:00",
                            style = Type.LabelSmall,
                            color = Color(0xFF888888)
                        )
                        Text(
                            text = "0:00",
                            style = Type.LabelSmall,
                            color = Color(0xFF888888)
                        )
                    }
                }
        }
    }
}