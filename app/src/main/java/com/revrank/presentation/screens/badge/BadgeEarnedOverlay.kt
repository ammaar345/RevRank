package com.revrank.presentation.screens.badge

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.domain.model.BadgeType
import com.revrank.presentation.screens.score.ParticleBurst
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import kotlinx.coroutines.delay

/**
 * Full-screen overlay when a new badge is earned.
 * Shows particle burst, badge name, and description.
 */
@Composable
fun BadgeEarnedOverlay(
    badge: BadgeType,
    onDismiss: () -> Unit
) {
    val scale = animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f)
    ).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Particle burst behind badge
        ParticleBurst(
            modifier = Modifier.fillMaxSize(),
            color = MatrixGreen,
            particleCount = 24
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            Icon(
                imageVector = com.revrank.presentation.components.RevRankIcons.Star,
                contentDescription = null,
                tint = MatrixGreen,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = badge.displayName,
                fontSize = 24.sp,
                color = MatrixGreen,
                fontFamily = Rajdhani
            )

            Text(
                text = badge.description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f), // TextSecondary
                fontFamily = Rajdhani
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsing "TAP TO CONTINUE"
            val alpha = animateFloatAsState(targetValue = 0.6f, animationSpec = tween(800)).value
            Text(
                text = "TAP TO CONTINUE",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = alpha)
            )
        }
    }
}

// ─── PREVIEW ───
@androidx.compose.ui.tooling.preview.Preview(device = "id:pixel_5", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BadgeEarnedOverlayPreview() {
    BadgeEarnedOverlay(
        badge = com.revrank.domain.model.BadgeType.PERFECT_RUN,
        onDismiss = {}
    )
}
