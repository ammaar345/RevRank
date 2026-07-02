package com.revrank.presentation.screens.paywall

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.screens.score.MatrixRainReveal
import com.revrank.presentation.theme.*
import kotlinx.coroutines.delay

/**
 * Full-screen success overlay shown after a successful Pro purchase.
 * Shows matrix rain animation, then resolves to "PRO ACTIVATED".
 * Auto-dismisses to the previous screen after 3 seconds.
 */
@Composable
fun ProActivatedScreen(
    onAutoDismiss: () -> Unit
) {
    var phase by remember { mutableStateOf(0) } // 0 = rain, 1 = text, 2 = dismiss

    // Animate through phases
    LaunchedEffect(Unit) {
        delay(400)
        phase = 1
        delay(1600)
        phase = 2
        delay(1000)
        onAutoDismiss()
    }

    val textAlpha = remember { Animatable(0f) }
    LaunchedEffect(phase) {
        if (phase >= 1) {
            textAlpha.animateTo(1f, animationSpec = tween(600))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Void),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Phase 0: Matrix rain animation resolving to "PRO"
            if (phase >= 0) {
                MatrixRainReveal(
                    text = "PRO",
                    color = MatrixGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Phase 1+: "PRO ACTIVATED" text
            if (phase >= 1) {
                Text(
                    text = "PRO ACTIVATED",
                    color = MatrixGreen,
                    fontSize = 28.sp,
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha.value)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "All features unlocked. Drive well.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha.value)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progress indicator for auto-dismiss
                Text(
                    text = "Returning...",
                    color = TextDim,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.alpha((textAlpha.value * 0.6f).coerceAtLeast(0f))
                )
            }
        }
    }
}
