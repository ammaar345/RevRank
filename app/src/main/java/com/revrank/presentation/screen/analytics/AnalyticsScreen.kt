package com.revrank.presentation.screen.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.Color
import com.revrank.presentation.theme.Type

/**
 * Analytics screen with three tabs: SCORES, DISTANCE, CATEGORIES.
 * Each tab shows a placeholder chart (to be replaced with actual charting library).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    selectedTab: String = "Scores",
    onTabSelected: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Analytics",
                        style = Type.TitleLarge,
                        color = Color.White
                    )
                },
                backgroundColor = Color(0xFF000000),
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedIndex = when (selectedTab) {
                    "Scores" -> 0
                    "Distance" -> 1
                    "Categories" -> 2
                    else -> 0
                },
                indicator = { tabPositions ->
                    TabRowDefaults.DrawerIndicator(
                        color = Color(0xFF00FF41), // MatrixGreen
                        thickness = 2.dp
                    )
                },
                contentColor = Color.White,
                indicatorContainerColor = Color.Transparent
            ) {
                Tab(
                    text = { Text("SCORES", style = Type.LabelMedium) },
                    selected = selectedTab == "Scores",
                    onClick = { onTabSelected("Scores") }
                )
                Divider(color = Color(0xFF1E1E1E), thickness = 1.dp)
                Tab(
                    text = { Text("DISTANCE", style = Type.LabelMedium) },
                    selected = selectedTab == "Distance",
                    onClick = { onTabSelected("Distance") }
                )
                Divider(color = Color(0xFF1E1E1E), thickness = 1.dp)
                Tab(
                    text = { Text("CATEGORIES", style = Type.LabelMedium) },
                    selected = selectedTab == "Categories",
                    onClick = { onTabSelected("Categories") }
                )
            }

            // Content
            when (selectedTab) {
                "Scores" -> ScoresTabContent()
                "Distance" -> DistanceTabContent()
                "Categories" -> CategoriesTabContent()
            }
        }
    }
}

@Composable
fun ScoresTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Scores Trend",
            style = Type.TitleMedium,
            color = Color.White
        )
        // Placeholder for line chart (last 30 trips)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF111111), shape = RectangleShape)
        ) {
            Text(
                text = "Line Chart: Score over last 30 trips\n(Moving average overlay)",
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = "Tap a point to see trip summary tooltip",
            style = Type.LabelSmall,
            color = Color(0xFF888888)
        )
    }
}

@Composable
fun DistanceTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Distance Heatmap",
            style = Type.TitleMedium,
            color = Color.White
        )
        // Placeholder for calendar heatmap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF111111), shape = RectangleShape)
        ) {
            Text(
                text = "Calendar Heatmap: Last 3 months\nEach day = distance driven",
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = "Tap a day to see trips that day",
            style = Type.LabelSmall,
            color = Color(0xFF888888)
        )
    }
}

@Composable
fun CategoriesTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Driving Categories Radar",
            style = Type.TitleMedium,
            color = Color.White
        )
        // Placeholder for radar chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFF111111), shape = RectangleShape)
        ) {
            Text(
                text = "Radar Chart: 5 axes\n(acceleration/braking/cornering/smoothness/consistency)\nCurrent month vs All time",
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = "Shows two polygons: current month (green) and all time (dim white)",
            style = Type.LabelSmall,
            color = Color(0xFF888888)
        )
    }
}