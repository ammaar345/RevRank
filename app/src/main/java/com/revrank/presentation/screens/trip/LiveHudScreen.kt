package com.revrank.presentation.screens.trip

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revrank.domain.model.DrivingQuality
import com.revrank.presentation.components.TerminalScaffold
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.viewmodel.LiveHudViewModel
import com.revrank.service.TripTrackingService

/**
 * Live HUD shown while a trip is being tracked: big speed readout,
 * quality-tinted glow, distance/score stats, and an end-trip FAB.
 */
@Composable
fun LiveHudScreen(
    viewModel: LiveHudViewModel = hiltViewModel(),
    onTripEnded: () -> Unit = {}
) {
    val context = LocalContext.current
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    val score by viewModel.score.collectAsStateWithLifecycle()
    val distanceKm by viewModel.distanceKm.collectAsStateWithLifecycle()
    val drivingQuality by viewModel.drivingQuality.collectAsStateWithLifecycle()

    val glowColor = when (drivingQuality) {
        DrivingQuality.SMOOTH -> MatrixGreen
        DrivingQuality.MODERATE -> Color(0xFFFFA500)
        DrivingQuality.AGGRESSIVE -> Color(0xFFFF453A)
    }

    TerminalScaffold(screenId = "LIVE // TRACKING") {
        // Quality-tinted glow behind the speed readout
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.2f),
                            glowColor.copy(alpha = 0f)
                        )
                    )
                )
        )

        Text(
            text = "${speed.toInt()}",
            color = Color.White,
            fontSize = 96.sp,
            fontFamily = ShareTechMono,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(280.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Distance",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${"%.1f".format(distanceKm)} km",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = Rajdhani
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Score",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "$score",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = Rajdhani
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                val stopIntent = Intent(context, TripTrackingService::class.java).apply {
                    action = TripTrackingService.ACTION_STOP
                }
                ContextCompat.startForegroundService(context, stopIntent)
                onTripEnded()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "End Trip",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
