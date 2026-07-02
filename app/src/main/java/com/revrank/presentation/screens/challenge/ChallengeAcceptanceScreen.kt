package com.revrank.presentation.screens.challenge

import androidx.compose.foundation.BorderStroke
import androidx.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.circle
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.revrank.R
import com.revrank.domain.model.ChallengeAcceptance
import com.revrank.domain.model.RouteChallenge
import com.revrank.presentation.components.RankBadge
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.viewmodel.challenge.ChallengeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeAcceptanceScreen(
    challengeId: String,
    onChallengeAccepted: () -> Unit = {}
) {
    val viewModel: ChallengeViewModel = viewModel()
    val challenge by viewModel.challenge.collectAsState()
    val isAccepting by viewModel.isAccepting.collectAsState()
    val error by viewModel.error.collectAsState()

    // If challenge hasn't loaded yet, show a placeholder
    if (challenge == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            CircularProgressIndicator(
                color = MatrixGreen,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    // Check if the current user has already accepted this challenge
    // For simplicity, we'll assume we have a way to get the current user ID.
    // In a real app, we would get this from the ViewModel or a repository.
    // Here, we'll just check if the challenge's acceptances list contains the current user.
    // We don't have the current user ID in this screen, so we'll skip this check for now.
    // We'll rely on the ViewModel to handle duplicate attempts.

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CHALLENGE", fontSize = 20.sp, fontFamily = ShareTechMono, color = Color.White) },
                backgroundColor = Color.Transparent
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            // Challenger info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFF111111))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Challenger's avatar (placeholder)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Challenge.Companion.randomColor().copy(alpha = 0.2f))
                        .border(1.dp, Challenge.Companion.randomColor(), CircleShape)
                ) {
                    Text(
                        text = challenge.creatorUsername.first().uppercase(),
                        fontSize = 20.sp,
                        fontFamily = ShareTechMono,
                        color = Challenge.Companion.randomColor(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "CHALLENGE FROM",
                        fontSize = 10.sp,
                        color = Color(0xFF888888),
                        fontFamily = Rajdhani,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = challenge.creatorUsername,
                        fontSize = 18.sp,
                        fontFamily = ShareTechMono,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // Challenge score
                Text(
                    text = "${challenge.creatorScore}",
                    fontSize = 32.sp,
                    fontFamily = ShareTechMono,
                    color = challenge.creatorScore.toGradeColor()
                )
                Text(
                    text = "SCORE",
                    fontSize = 10.sp,
                    fontFamily = Rajdhani,
                    color = Color(0xFF888888),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Challenge details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111111))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "CAN YOU BEAT THIS SCORE ON THIS ROUTE?",
                        fontSize = 14.sp,
                        color = Color(0xFF888888),
                        fontFamily = Rajdhani,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Drive the same route (start/end points within 500m) and score higher than ${challenge.creatorScore} to win.",
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        fontFamily = Rajdhani
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accept button
            Button(
                onClick = {
                    // TODO: Get current user ID and username from auth or viewModel
                    // For now, we'll use placeholder values.
                    val userId = "current_user_id" // Replace with actual
                    val username = "current_user" // Replace with actual
                    viewModel.acceptChallenge(challengeId, userId, username)
                },
                enabled = !isAccepting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAccepting) Color(0xFF333333) else MatrixGreen,
                    contentColor = if (isAccepting) Color.White else Color.Black
                )
            ) {
                if (isAccepting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "ACCEPT CHALLENGE",
                        fontSize = 16.sp,
                        fontFamily = ShareTechMono,
                        color = Color.Black
                    )
                }
            }

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = Rajdhani,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Start)
                )
            }
        }
    }
}

// Companion object to generate a random color for the avatar placeholder (for demo)
private object Challenge {
    private val colors = listOf(
        Color(0xFFFF0000), Color(0xFFFF00FF), Color(0xFF00FFFF),
        Color(0xFFFFFF00), Color(0xFF00FF00), Color(0xFF0000FF)
    )
    fun randomColor() = colors.random()
}