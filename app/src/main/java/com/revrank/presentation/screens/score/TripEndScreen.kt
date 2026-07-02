package com.revrank.presentation.screens.score

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Trip
import com.revrank.domain.model.User
import com.revrank.domain.model.XPCalculator
import com.revrank.domain.model.toGrade
import com.revrank.domain.model.toGradeColor
import com.revrank.presentation.screens.badge.BadgeEarnedOverlay
import com.revrank.presentation.screens.share.ShareCardGenerator
import com.revrank.presentation.components.CircularScoreProgress
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun TripEndScreen(
    trip: Trip,
    user: User,
    newBadges: List<BadgeType> = emptyList(),
    xpGained: Int = 0,
    onShare: (Bitmap) -> Unit = {},
    onViewHistory: () -> Unit = {},
    onBadgeDismissed: () -> Unit = {}
) {
    val context = LocalContext.current
    val score = trip.score
    val grade = score.toGrade()
    val gradeColor = score.toGradeColor()
    val scope = rememberCoroutineScope()
    val challengeViewModel: ChallengeViewModel = hiltViewModel()

    // Animation phases
    var phase by remember { mutableIntStateOf(0) } // 0: Matrix rain, 1: Score reveal, 2: Grade, 3: Stats, 4: XP, 5: Buttons
    var showBadgeOverlay by remember { mutableStateOf(false) }
    var badgeIndex by remember { mutableIntStateOf(0) }

    // State for challenge creation
    val isCreating by challengeViewModel.isAccepting.collectAsState()
    val createdChallenge by challengeViewModel.challenge.collectAsState()
    val createError by challengeViewModel.error.collectAsState()

    // Start animation sequence
    LaunchedEffect(Unit) {
        delay(800)   // Phase 1: Matrix rain
        phase = 1
        delay(700)   // Phase 2: Grade letter
        phase = 2
        delay(700)   // Phase 3: Stats
        phase = 3
        delay(1500)  // Phase 4: XP
        phase = 4
        delay(800)   // Phase 5: Buttons
        phase = 5

        // Show badges after main animation (if no auto dismiss)
        if (newBadges.isNotEmpty()) {
            delay(500)
            showBadgeOverlay = true
        }
    }

    // Handle challenge creation completion
    LaunchedEffect(createdChallenge) {
        if (createdChallenge != null) {
            val challengeId = createdChallenge!!.id
            val link = "reverank.app/challenge/$challengeId"
            shareLink(link, context)
            // Clear the challenge state to avoid re-sharing on recomposition
            viewModelScope.launch {
                challengeViewModel.clearChallenge()
            }
        }
    }

    // Show error toast if creation failed (simplified - in production use a proper Snackbar/toast)
    LaunchedEffect(createError) {
        if (createError != null) {
            Toast.makeText(context, createError, Toast.LENGTH_SHORT).show()
            // Clear error after showing
            viewModelScope.launch {
                delay(3000)
                challengeViewModel.clearError()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ─── BADGE OVERLAY (shown when triggered) ───
        if (showBadgeOverlay && badgeIndex < newBadges.size) {
            BadgeEarnedOverlay(
                badge = newBadges[badgeIndex],
                onDismiss = {
                    badgeIndex++
                    if (badgeIndex >= newBadges.size) {
                        showBadgeOverlay = false
                        onBadgeDismissed()
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ─── RADIAL SCORE CIRCLE ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                if (phase >= 1) {
                    CircularScoreProgress(
                        score = score,
                        size = 240.dp,
                        strokeWidth = 8.dp,
                        glowColor = gradeColor,
                        trackColor = Color(0xFF222222),
                        durationMs = when (phase) { 1 -> 1200 else -> 0 }
                    )
                }
            }

            // ─── HORIZONTAL DIVIDER ───
            if (phase >= 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF333333))
                )
            }

            // ─── SCORE CATEGORY BARS ───
            if (phase >= 3) {
                Spacer(modifier = Modifier.height(8.dp))
                ScoreCategoryBar(label = "ACCELERATION", value = trip.scoreAcceleration, color = gradeColor, delay = 0)
                ScoreCategoryBar(label = "BRAKING", value = trip.scoreBraking, color = gradeColor, delay = 80)
                ScoreCategoryBar(label = "CORNERING", value = trip.scoreCornering, color = gradeColor, delay = 160)
                ScoreCategoryBar(label = "SMOOTHNESS", value = trip.scoreSmoothness, color = gradeColor, delay = 240)
                ScoreCategoryBar(label = "CONSISTENCY", value = trip.scoreConsistency, color = gradeColor, delay = 320)
                Spacer(modifier = Modifier.height(8.dp))

                // ─── STATS ROW (Distance / Time / Max Speed) ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val durationMin = ((trip.endTime ?: 0L) - trip.startTime) / 1000 / 60
                    StatsItem("DIST", "%.1f km".format(trip.distanceKm))
                    StatsItem("TIME", "${durationMin}min")
                    StatsItem("TOP SPEED", "%.0f km/h".format(trip.maxSpeedKmh))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ─── XP GAIN ───
            if (phase >= 4) {
                Text(
                    text = "+${xpGained} XP",
                    fontSize = 24.sp,
                    color = MatrixGreen,
                    fontFamily = ShareTechMono,
                    modifier = Modifier
                        .alpha(animateFloatAsState(targetValue = 1f, animationSpec = tween(500)).value)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ─── BUTTONS ───
            if (phase >= 5) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        scope.launch {
                            // Generate share card
                            val bitmap = ShareCardGenerator.generate(trip, user)
                            onShare(bitmap)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = ButtonDefaults.outlinedButtonBorder().copy(width = 2.dp, brush = Brush.solidColor(MatrixGreen)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SHARE", color = MatrixGreen, fontFamily = Rajdhani, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModelScope.launch {
                            try {
                                challengeViewModel.createChallenge(trip, user)
                                // The LaunchedEffect above will handle sharing once the challenge is created
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Error will be caught by the challengeViewModel.error StateFlow and shown via LaunchedEffect
                            }
                        }
                    },
                    enabled = !isCreating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = ButtonDefaults.outlinedButtonBorder().copy(width = 2.dp, brush = Brush.solidColor(MatrixGreen)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(
                            color = MatrixGreen,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("CHALLENGE A FRIEND", color = MatrixGreen, fontFamily = Rajdhani, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onViewHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VIEW HISTORY", color = Color(0xFF888888), fontFamily = Rajdhani, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Helper function to share a link via implicit intent
@Composable
private fun shareLink(link: String, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, link)
    }
    val chooser = Intent.createChooser(intent, "Share via")
    if (intent.resolveActivity(context.packageManager) != null) {
        ContextCompat.startActivity(
            context,
            Intent.createChooser(intent, "Share via"),
            null
        )
    }
}
}

@Composable
private fun ScoreCategoryBar(label: String, value: Int, color: Color, delay: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = value / 100f,
        animationSpec = tween(durationMillis = 700, delayMillis = delay, easing = LinearEasing)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF888888), // TextSecondary
            fontFamily = Rajdhani,
            modifier = Modifier.weight(1.5f)
        )
        // Progress bar
        Box(
            modifier = Modifier
                .weight(4f)
                .height(8.dp)
                .background(Color(0xFF222222))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(color)
            )
        }
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            color = Color.White,
            fontFamily = ShareTechMono,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatsItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, color = Color.White, fontFamily = ShareTechMono)
        Text(text = label, fontSize = 12.sp, color = Color(0xFF888888), fontFamily = Rajdhani)
    }
}

// ─── PREVIEW ───
@androidx.compose.ui.tooling.preview.Preview(device = "id:pixel_5", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun TripEndScreenPreview() {
    val previewTrip = com.revrank.domain.model.Trip(
        id = "preview-1",
        userId = "user-1",
        startTime = System.currentTimeMillis() - 1800000,
        endTime = System.currentTimeMillis(),
        distanceKm = 14.2f,
        maxSpeedKmh = 95f,
        avgSpeedKmh = 48f,
        score = 84,
        scoreAcceleration = 82,
        scoreBraking = 88,
        scoreCornering = 79,
        scoreSmoothness = 90,
        scoreConsistency = 85,
        routeName = null,
        shareImagePath = null
    )
    val previewUser = com.revrank.domain.model.User(
        id = "user-1",
        username = "TestUser",
        email = "test@test.com",
        displayName = "Test User",
        vehicleType = "CAR",
        xp = 1200,
        rank = com.revrank.domain.model.Rank.CAPTAIN,
        badges = emptyList(),
        isPro = false,
        createdAt = System.currentTimeMillis()
    )
    TripEndScreen(
        trip = previewTrip,
        user = previewUser,
        newBadges = emptyList(),
        xpGained = 245
    )
}
