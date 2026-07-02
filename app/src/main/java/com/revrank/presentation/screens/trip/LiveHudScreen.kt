package com.revrank.presentation.screens.trip

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Background
import androidx.compose.foundation.border.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.revrank.R
import com.revrank.data.repository.TripTrackingRepository
import com.revrank.domain.model.DrivingQuality
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import dagger.hilt.android.AndroidEntryPoint
import hilt.viewmodel.*
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class LiveHudScreen(
    private val onTripEnded: () -> Unit = {}
) : androidx.compose.runtime.Composable() {

    private val context = LocalContext.current
    private val tripTrackingRepository: TripTrackingRepository = hiltViewModel()
    private val speed by tripTrackingRepository.currentSpeed.collectAsStateWithLifecycle()
    private val score by tripTrackingRepository.currentScore.collectAsStateWithLifecycle()
    private val distanceKm by tripTrackingRepository.distanceKm.collectAsStateWithLifecycle()
    private val drivingQuality by tripTrackingRepository.drivingQuality.collectAsStateWithLifecycle()

    // Determine glow color based on driving quality
    private val glowColor by remember {
        derivedStateOf {
            when (drivingQuality) {
                DrivingQuality.SMOOTH -> MatrixGreen
                DrivingQuality.MODERATE -> Color(0xFFFFA500) // Amber
                DrivingQuality.AGGRESSIVE -> Color(Color.Red)
            }
        }
    }

    // Request to ignore battery optimizations if not already ignored (do once)
    var ignoreBatteryRequested by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!ignoreBatteryRequested) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                // Show a system dialog to ignore battery optimizations
                val intent = android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                // We can't start activity for result from composable easily, so we'll just show a snackbar or dialog.
                // For simplicity, we'll just note that we asked and let the user go to settings manually if needed.
                // In a real app, we might use Accompanist Permission or show a custom dialog.
                // We'll just set the flag to true so we don't bug them every time.
                ignoreBatteryRequested = true
                // Optionally, show a Snackbar or Dialog here.
            } else {
                ignoreBatteryRequested = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background glow effect - a circle behind the speedometer
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowsColor.copy(alpha = 0.2f),
                            glowsColor.copy(alpha = 0f)
                        ),
                        center = Alignment.Center,
                        radius = 125f
                    )
                )
        )

        // Speed display (large text)
        Text(
            text = "${speed.toInt()}",
            color = Color.White,
            fontSize = 96.sp,
            fontFamily = ShareTechMono,
            modifier = Modifier
                .align(Alignment.Center)
        )

        // Bottom stats row
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spaceBetween,
                modifier = Modifier
                    .width(280.dp)
            ) {
                // Distance
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                // Score
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Score",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${score}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = Rajdhani
                    )
                }
            }
        }

        // End Trip FAB (large)
        FloatingActionButton(
            onClick = {
                // Stop tracking service
                val stopIntent = Intent(context, TripTrackingService::class.java).apply {
                    action = TripTrackingService.ACTION_STOP
                }
                ContextCompat.startForegroundService(context, stopIntent)
                // Notify parent to navigate away
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