package com.revrank.presentation.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.domain.model.Rank
import com.revrank.domain.model.WeeklyChallenge
import com.revrank.presentation.components.RankBadge
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * HomeScreen — the main dashboard.
 * Sections: Greeting, Rank Progress, Quick Stats, Weekly Challenges, Last Trip, FAB.
 */
@Composable
fun HomeScreen(
    username: String,
    xp: Int,
    rank: Rank,
    streakDays: Int,
    weeklyTrips: Int,
    weeklyKm: Float,
    challenges: List<WeeklyChallenge>,
    lastTrip: LastTripSummary?,
    onStartTrip: () -> Unit,
    onViewRanks: () -> Unit,
    onViewTrip: (String) -> Unit = {}
) {
    val greeting = rememberGreeting(username)
    val xpProgress = Rank.progressToNext(xp)
    val nextRank = remember {
        val nextIndex = rank.ordinal + 1
        if (nextIndex < Rank.entries.size) Rank.entries[nextIndex] else null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ─── GREETING ───
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = greeting,
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = Rajdhani
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ─── RANK PROGRESS CARD ───
        item {
            RankProgressCard(rank = rank, xp = xp, xpProgress = xpProgress, nextRank = nextRank, onClick = onViewRanks)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ─── QUICK STATS ROW ───
        item {
            QuickStatsRow(streakDays = streakDays, weeklyTrips = weeklyTrips, weeklyKm = weeklyKm)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ─── WEEKLY CHALLENGES ───
        item {
            if (challenges.isNotEmpty()) {
                Text(
                    text = "WEEKLY CHALLENGES",
                    fontSize = 11.sp,
                    color = Color(0xFF888888),
                    fontFamily = Rajdhani,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        items(challenges.size) { index ->
            ChallengeRow(challenge = challenges[index])
        }

        if (challenges.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // ─── LAST TRIP ───
        item {
            if (lastTrip != null) {
                LastTripCard(trip = lastTrip, onView = { onViewTrip(lastTrip.id) })
            } else {
                // First-time CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111))
                        .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO TRIPS YET",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            fontFamily = ShareTechMono,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take your first drive to see your stats",
                            fontSize = 12.sp,
                            color = Color(0xFF888888),
                            fontFamily = Rajdhani
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }

    // ─── START TRIP FAB ───
    FloatingActionButton(
        onClick = onStartTrip,
        containerColor = MatrixGreen,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "▶ START TRIP",
            fontSize = 14.sp,
            fontFamily = ShareTechMono,
            color = Color.Black,
            letterSpacing = 3.sp
        )
    }
}

// ─── Helpers ───

@Composable
private fun rememberGreeting(username: String): String {
    val hour = java.util.Calendarnip.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 5..11 -> "morning"
        in 12..17 -> "afternoon"
        in 18..22 -> "evening"
        else -> "night"
    }
    return "Good $timeGreeting, $username"
}

@Composable
private fun RankProgressCard(rank: Rank, xp: Int, xpProgress: Float, nextRank: Rank?, onClick: () -> Unit) {
    val animatedProgress by animateFloatAsState(targetValue = xpProgress, animationSpec = tween(1000))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111111))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RankBadge(rank = rank, size = 40.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = rank.displayName.uppercase(),
                        fontSize = 18.sp,
                        fontFamily = ShareTechMono,
                        color = rank.color
                    )
                    Text(
                        text = "Rank ${rank.level} of 7",
                        fontSize = 11.sp,
                        color = Color(0xFF888888),
                        fontFamily = Rajdhani
                    )
                }
            }

            // XP Progress bar
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MatrixGreen,
                    trackColor = Color(0xFF222222)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (nextRank != null) {
                Text(
                    text = "$xp / ${nextRank.xpRequired} XP  ·  ${nextRank.xpRequired - xp} XP to ${nextRank.displayName}",
                    fontSize = 11.sp,
                    color = Color(0xFF888888),
                    fontFamily = ShareTechMono
                )
            } else {
                Text(
                    text = "$xp XP  ·  MAX RANK",
                    fontSize = 11.sp,
                    color = MatrixGreen,
                    fontFamily = ShareTechMono
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(streakDays: Int, weeklyTrips: Int, weeklyKm: Float) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Streak card
        StatCard(
            modifier = Modifier.weight(1f),
            label = "STREAK",
            value = if (streakDays > 0) "$streakDays day${if (streakDays > 1) "s" else ""}" else "Start a streak!",
            valueColor = if (streakDays > 0) MatrixGreen else Color(0xFF888888)
        )
        Spacer(modifier = Modifier.width(10.dp))
        // Weekly stats card
        StatCard(
            modifier = Modifier.weight(1f),
            label = "THIS WEEK",
            value = "$weeklyTrips trips · %.1f km".format(weeklyKm),
            valueColor = Color.White
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, valueColor: Color) {
    Column(
        modifier = modifier
            .background(Color(0xFF111111))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
            .padding(14.dp)
    ) {
        Text(text = label, fontSize = 9.sp, color = Color(0xFF888888), fontFamily = Rajdhani, letterSpacing = 2.sp)
        Text(text = value, fontSize = 14.sp, color = valueColor, fontFamily = ShareTechMono, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun ChallengeRow(challenge: WeeklyChallenge) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = challenge.title, fontSize = 13.sp, color = Color.White, fontFamily = Rajdhani)
            Text(text = challenge.description, fontSize = 10.sp, color = Color(0xFF888888), fontFamily = Rajdhani)
            Spacer(modifier = Modifier.height(4.dp))
            // Progress bar
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF222222), RoundedCornerShape(2.dp))) {
                Box(modifier = Modifier.fillMaxWidth(challenge.progress).fillMaxHeight().background(MatrixGreen, RoundedCornerShape(2.dp)))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "${(challenge.progress * 100).toInt()}%", fontSize = 12.sp, color = Color(0xFF888888), fontFamily = ShareTechMono)
    }
}

@Composable
private fun LastTripCard(trip: LastTripSummary, onView: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111111))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "LAST TRIP", fontSize = 11.sp, color = Color(0xFF888888), fontFamily = Rajdhani, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = trip.date, fontSize = 11.sp, color = Color(0xFF888888), fontFamily = ShareTechMono)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${trip.distanceKm}km", fontSize = 16.sp, color = Color.White, fontFamily = ShareTechMono)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "•", color = Color(0xFF888888))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Score ${trip.score}", fontSize = 16.sp, color = trip.gradeColor, fontFamily = ShareTechMono)
            }
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = onView, modifier = Modifier.align(Alignment.End)) {
                Text(text = "VIEW", fontSize = 12.sp, color = MatrixGreen, fontFamily = ShareTechMono)
            }
        }
    }
}

/** Summary of last trip for display on home screen. */
data class LastTripSummary(
    val id: String,
    val date: String,
    val distanceKm: Float,
    val score: Int,
    val gradeColor: Color
)
