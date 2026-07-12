package com.revrank.presentation.screens.profile

import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revrank.domain.model.Rank
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.viewmodel.PublicProfileViewModel

/**
 * Public profile reached via reverank.app/profile/{username} deep links.
 */
@Composable
fun PublicProfileScreen(
    username: String,
    viewModel: PublicProfileViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(username) { viewModel.loadUserByUsername(username) }

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
                text = "PROFILE",
                fontFamily = ShareTechMono,
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }

        val user = uiState.user
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MatrixGreen, strokeWidth = 2.dp)
            }

            user == null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: "USER NOT FOUND",
                    fontFamily = ShareTechMono,
                    fontSize = 14.sp,
                    color = Color(0xFF888888)
                )
            }

            else -> {
                val rank = Rank.entries.getOrNull(user.rank - 1) ?: Rank.LEARNER
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar initial
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(rank.color.copy(alpha = 0.12f))
                            .border(1.5.dp, rank.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.username.take(1).uppercase(),
                            fontFamily = ShareTechMono,
                            fontSize = 36.sp,
                            color = rank.color
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "@${user.username}",
                        fontFamily = ShareTechMono,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Text(
                        text = rank.displayName.uppercase(),
                        fontFamily = ShareTechMono,
                        fontSize = 13.sp,
                        color = rank.color,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(value = user.xp.toString(), label = "XP")
                        ProfileStat(value = user.totalTrips.toString(), label = "TRIPS")
                        ProfileStat(
                            value = "%.0f".format(user.totalDistanceKm),
                            label = "KM"
                        )
                        ProfileStat(value = user.streakDays.toString(), label = "STREAK")
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out @${user.username} on RevRank — reverank.app/profile/${user.username}"
                                )
                            }
                            context.startActivity(Intent.createChooser(intent, "Share profile"))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MatrixGreen
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MatrixGreen),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SHARE PROFILE",
                            fontFamily = ShareTechMono,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = ShareTechMono,
            fontSize = 22.sp,
            color = Color.White
        )
        Text(
            text = label,
            fontFamily = Rajdhani,
            fontSize = 11.sp,
            color = Color(0xFF888888),
            letterSpacing = 1.sp
        )
    }
}
