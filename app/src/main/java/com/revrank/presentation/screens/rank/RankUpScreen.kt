package com.revrank.presentation.screens.rank

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.domain.model.Rank
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono
import kotlinx.coroutines.delay

/**
 * Full-screen screen shown when the user ranks up.
 */
@Composable
fun RankUpScreen(
    newRank: Rank,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
        delay(3000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        val scale = animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 600)
        ).value

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "RANK UP",
                fontSize = 24.sp,
                color = Color(0xFF888888),
                fontFamily = Rajdhani
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = newRank.displayName.uppercase(),
                fontSize = 48.sp,
                color = newRank.color,
                fontFamily = ShareTechMono,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "YOU ARE NOW ${newRank.displayName.uppercase()}",
                fontSize = 18.sp,
                color = newRank.color,
                fontFamily = ShareTechMono
            )
        }
    }
}

// ─── PREVIEW ───
@androidx.compose.ui.tooling.preview.Preview(device = "id:pixel_5", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RankUpScreenPreview() {
    RankUpScreen(
        newRank = com.revrank.domain.model.Rank.APEX_DRIVER,
        onDismiss = {}
    )
}
