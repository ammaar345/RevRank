package com.revrank.presentation.screen.analytics

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.domain.model.Trip
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.viewmodel.analytics.AnalyticsViewModel

/**
 * Analytics screen (Pro): SCORES / DISTANCE / CATEGORIES tabs.
 * Charts are simple Compose placeholders until a charting library lands.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val trips by viewModel.trips.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("SCORES", "DISTANCE", "CATEGORIES")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "ANALYTICS",
                fontFamily = ShareTechMono,
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Black,
            contentColor = MatrixGreen
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = label,
                            fontFamily = Rajdhani,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) MatrixGreen else Color(0xFF888888)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> ScoresTab(trips)
            1 -> DistanceTab(trips)
            2 -> CategoriesTab(trips)
        }
    }
}

@Composable
private fun ScoresTab(trips: List<Trip>) {
    val recent = trips.filter { it.endTime != null }.takeLast(14)
    ChartFrame(title = "SCORE TREND — LAST ${recent.size} TRIPS") {
        BarPlaceholder(values = recent.map { it.score / 100f }, color = MatrixGreen)
    }
}

@Composable
private fun DistanceTab(trips: List<Trip>) {
    val recent = trips.filter { it.endTime != null }.takeLast(14)
    val maxKm = (recent.maxOfOrNull { it.distanceKm } ?: 1.0).coerceAtLeast(1.0)
    ChartFrame(title = "DISTANCE PER TRIP (KM)") {
        BarPlaceholder(
            values = recent.map { (it.distanceKm / maxKm).toFloat() },
            color = Color(0xFF00B4D8)
        )
    }
}

@Composable
private fun CategoriesTab(trips: List<Trip>) {
    val done = trips.filter { it.endTime != null }
    val categories = listOf(
        "ACCELERATION" to done.map { it.scoreAcceleration }.averageOrZero(),
        "BRAKING" to done.map { it.scoreBraking }.averageOrZero(),
        "CORNERING" to done.map { it.scoreCornering }.averageOrZero(),
        "SMOOTHNESS" to done.map { it.scoreSmoothness }.averageOrZero(),
        "CONSISTENCY" to done.map { it.scoreConsistency }.averageOrZero()
    )
    Column(modifier = Modifier.padding(16.dp)) {
        categories.forEach { (label, avg) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontFamily = Rajdhani,
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.width(110.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(Color(0xFF222222))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((avg / 100f).coerceIn(0f, 1f))
                            .height(5.dp)
                            .background(MatrixGreen)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = avg.toInt().toString(),
                    fontFamily = ShareTechMono,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ChartFrame(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = title,
            fontFamily = ShareTechMono,
            fontSize = 10.sp,
            color = Color(0xFF888888),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF0A0A0A))
                .border(1.dp, Color(0xFF1E3A2A))
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun BarPlaceholder(values: List<Float>, color: Color) {
    if (values.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "NO TRIP DATA YET",
                fontFamily = ShareTechMono,
                fontSize = 11.sp,
                color = Color(0xFF555555)
            )
        }
        return
    }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEach { v ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height((160 * v.coerceIn(0.05f, 1f)).dp)
                    .background(color.copy(alpha = 0.75f))
            )
        }
    }
}

private fun List<Int>.averageOrZero(): Float =
    if (isEmpty()) 0f else (sum().toFloat() / size)
