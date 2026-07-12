@file:OptIn(ExperimentalMaterial3Api::class)

package com.revrank.presentation.screens.gforce

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.domain.model.GForcePoint
import com.revrank.presentation.components.ProGate
import com.revrank.presentation.screens.paywall.ProUpsellBanner
import com.revrank.presentation.theme.*
import androidx.compose.ui.graphics.drawscope.Stroke
import com.revrank.presentation.viewmodel.gforce.GForceViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * G-Force Visualizer Screen (Pro feature)
 * Shows a radar chart of lateral vs longitudinal G-forces over the trip,
 * with color-coded points by score and summary statistics.
 */
@Composable
fun GForceScreen(
    tripId: String,
    viewModel: GForceViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onUpgradeClick: () -> Unit = {}
) {
    // Pro-gated: show upsell banner if not Pro
    ProGate(
        content = {
            GForceContent(tripId = tripId, viewModel = viewModel, onDismiss = onDismiss)
        },
        fallback = {
            ProUpsellBanner(
                featureName = "G-force visualizer",
                featureDescription = "See your cornering forces with a live radar chart.",
                onSubscribe = onUpgradeClick,
                onDismiss = onDismiss
            )
        }
    )
}

@Composable
private fun GForceContent(
    tripId: String,
    viewModel: GForceViewModel,
    onDismiss: () -> Unit
) {
    val trip by viewModel.trip.collectAsStateWithLifecycle()
    val isLoading = trip == null

    // Scaffold with top app bar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("G-Force Visualizer", style = RevRankTypography.titleLarge, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF000000))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF000000))
        ) {
            if (isLoading) {
                // Loading placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF00FF41),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                val gForcePoints = trip?.gForcePoints ?: emptyList()
                if (gForcePoints.isEmpty()) {
                    // No data state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = "No data",
                            tint = Color(0xFF888888),
                            modifier = Modifier
                                .size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No G-force data available",
                            style = RevRankTypography.titleMedium,
                            color = Color(0xFF888888)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Drive more to collect data",
                            style = RevRankTypography.labelLarge,
                            color = Color(0xFF444444)
                        )
                    }
                } else {
                    // Main content: Chart + Stats
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: G-Force Chart
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(300.dp)
                        ) {
                            GForceChart(
                                points = gForcePoints,
                                modifier = Modifier
                                    .fillMaxSize()
                        )
                        }

                        // Right: Stats
                        Column(
                            modifier = Modifier
                                .width(120.dp)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "MAX G-FORCES",
                                style = RevRankTypography.labelSmall,
                                color = Color(0xFF888888)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lateral: ${"%.1f".format(getMaxLateralG(gForcePoints))}g",
                                style = RevRankTypography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Longitudinal: ${"%.1f".format(getMaxLongitudinalG(gForcePoints))}g",
                                style = RevRankTypography.titleMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "TIME IN SAFE ZONE",
                                style = RevRankTypography.labelSmall,
                                color = Color(0xFF888888)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${getSafeZonePercentage(gForcePoints)}%",
                                style = RevRankTypography.titleMedium,
                                color = Color(0xFF00FF41)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GForceChart(
    points: List<GForcePoint>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val axisLabelStyle = TextStyle(
        color = Color(0xFF888888),
        fontSize = 12.sp
    )

    Canvas(modifier = modifier) {
        val canvasSize = size
        val centerX = canvasSize.width / 2
        val centerY = canvasSize.height / 2
        val radius = kotlin.math.min(centerX, centerY) * 0.8f

        // Draw grid lines (concentric circles)
        val gridRadii = listOf(radius * 0.33f, radius * 0.66f, radius)
        val gridColors = listOf(
            Color(0x261E1E1E),
            Color(0x261E1E1E),
            Color(0x261E1E1E)
        )
        drawCircle(
            color = Color(0x1E1E1E1E),
            center = Offset(centerX, centerY),
            radius = radius * 0.3f  // Safe zone circle
        )
        for ((index, r) in gridRadii.withIndex()) {
            drawCircle(
                color = gridColors[index],
                center = Offset(centerX, centerY),
                radius = r,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw crosshairs
        drawLine(
            start = Offset(centerX - radius, centerY),
            end = Offset(centerX + radius, centerY),
            color = Color(0xFF1E1E1E),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            start = Offset(centerX, centerY - radius),
            end = Offset(centerX, centerY + radius),
            color = Color(0xFF1E1E1E),
            strokeWidth = 1.dp.toPx()
        )

        // Draw axis labels
        fun drawLabel(text: String, x: Float, y: Float) {
            val result = textMeasurer.measure(text, axisLabelStyle)
            drawText(
                textLayoutResult = result,
                topLeft = Offset(x, y)
            )
        }

        drawLabel("L", centerX - radius - 12, centerY - 6)
        drawLabel("R", centerX + radius + 4, centerY - 6)
        drawLabel("F", centerX - 6, centerY - radius - 16)
        drawLabel("B", centerX - 4, centerY + radius + 4)

        // Draw points
        if (points.isNotEmpty()) {
            // Find max G for scaling
            var maxAbsG = 0.1f
            for (point in points) {
                val absLateral = kotlin.math.abs(point.lateralG)
                val absLongitudinal = kotlin.math.abs(point.longitudinalG)
                maxAbsG = kotlin.math.max(maxAbsG, kotlin.math.max(absLateral, absLongitudinal))
            }
            val scale = radius / (maxAbsG * 1.1f)  // Add 10% padding

            for (point in points) {
                // Convert G-forces to Cartesian coordinates
                val x = centerX + (point.lateralG * scale)
                val y = centerY - (point.longitudinalG * scale)  // Negative because Y increases downward

                // Get score color
                val score = point.scoreAtMoment ?: 50
                val pointColor = when {
                    score >= 90 -> Color(0xFF00FF41)  // Green
                    score >= 75 -> Color(0xFF39FF14)  // Lime
                    score >= 55 -> Color(0xFFFFD700)  // Amber
                    score >= 35 -> Color(0xFFFF6B00)  // Orange
                    else -> Color(0xFFFF2D00)         // Red
                }

                // Draw point
                drawCircle(
                    color = pointColor.copy(alpha = 0.6f),
                    center = Offset(x, y),
                    radius = 3.dp.toPx()
                )
            }
        }
    }
}

private fun getMaxLateralG(points: List<GForcePoint>): Float {
    if (points.isEmpty()) return 0f
    return points.map { kotlin.math.abs(it.lateralG) }.maxOrNull() ?: 0f
}

private fun getMaxLongitudinalG(points: List<GForcePoint>): Float {
    if (points.isEmpty()) return 0f
    return points.map { kotlin.math.abs(it.longitudinalG) }.maxOrNull() ?: 0f
}

private fun getSafeZonePercentage(points: List<GForcePoint>): Int {
    if (points.isEmpty()) return 0
    val safeCount = points.count { 
        kotlin.math.sqrt(
            it.lateralG * it.lateralG + it.longitudinalG * it.longitudinalG
        ) <= 0.3
    }
    return safeCount * 100 / points.size
}