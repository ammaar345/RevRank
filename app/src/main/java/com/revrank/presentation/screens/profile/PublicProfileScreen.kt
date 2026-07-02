package com.revrank.presentation.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.revrank.R
import com.revrank.domain.model.User
import com.revrank.presentation.theme.*
import com.revrank.presentation.viewmodel.PublicProfileUiState
import com.revrank.presentation.viewmodel.PublicProfileViewModel
import com.revrank.presentation.viewmodel.ShareLinkUtils
import com.revrank.presentation.viewmodel.ViewModelUtils.viewModel
import dagger.hilt.android.androidEntryPoint
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PublicProfileScreen(
    username: String,
    onBack: () -> Unit = {},
    viewModel: PublicProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    viewModel.lifecycleScope.launchWhenStarted {
        viewModel.loadUserByUsername(username)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "@$username",
                        fontFamily = Rajdhani,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MatrixGreen
                        )
                    }
                },
                backgroundColor = Void,
                containerColor = Void
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Void),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MatrixGreen)
            }
        } else if (uiState.user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Void),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "User not found",
                    fontFamily = Rajdhani,
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }
        } else {
            val user = uiState.user!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Void),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with rank-colored border
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            RoundedCircleShape(
                                start = user.rankColorHex?.let { Color.parseColor(it) } ?: MatrixGreen
                            )
                        )
                ) {
                    // Placeholder for avatar image (if we had URLs, we'd load with Glide/Coil)
                    // For now, we show initials
                    Text(
                        text = user.username.first().uppercase(),
                        fontFamily = ShareTechMono,
                        fontSize = 32.sp,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Username and display name
                Text(
                    text = user.username,
                    fontFamily = ShareTechMono,
                    fontSize = 24.sp,
                    color = TextPrimary
                )
                if (user.displayName != user.username) {
                    Text(
                        text = user.displayName,
                        fontFamily = Rajdhani,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rank badge and XP
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank badge (small square with rank color)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                user.rankColorHex?.let { Color.parseColor(it) } ?: MatrixGreen
                            )
                    ) {
                        Text(
                            text = user.rank.toString(),
                            fontFamily = ShareTechMono,
                            fontSize = 10.sp,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Level ${user.rank}",
                        fontFamily = Rajdhani,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${user.xp} XP",
                        fontFamily = ShareTechMono,
                        fontSize = 14.sp,
                        color = MatrixGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats row: avg trips, km, etc. (simplified)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    StatItem(label = "TRIPS", value = "${user.totalTrips}")
                    Stat(label = "DISTANCE", value = "${user.totalDistanceKm} km")
                    Stat(label = "STREAK", value = "${user.streakDays} days")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action buttons: Challenge and Share
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Challenge button (only if not viewing own profile? we'll show for now)
                    Button(
                        onClick = {
                            // TODO: Navigate to challenge creation with this user as target
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "CHALLENGE",
                            fontFamily = Rajdhani,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                    // Share button
                    Button(
                        onClick = {
                            ShareLinkUtils.shareProfileLink(username = user.username)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(2.dp, MatrixGreen)
                    ) {
                        Text(
                            text = "SHARE PROFILE",
                            fontFamily = Rajdhani,
                            fontWeight = FontWeight.SemiBold,
                            color = MatrixGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = ShareTechMono,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Text(
            text = label,
            fontFamily = Rajdhani,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

/**
 * Helper shape for a circle with a colored border (the border is the background color of the Box).
 * We achieve this by having a Box with the border color as background, and then a smaller
 * inner Box with the surface color (or image) on top.
 * However, for simplicity, we are using a single color circle for the background and then
 * putting the initials on top. In a real app, we would load an image and overlay a ring.
 */
private class RoundedCircleShape(private val borderColor: Color) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(
        Path().apply {
            addOval(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height
            )
        }
    )
}