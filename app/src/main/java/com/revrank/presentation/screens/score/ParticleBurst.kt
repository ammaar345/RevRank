package com.revrank.presentation.screens.score

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.MatrixGreen
import kotlin.math.cos
import kotlin.math.sin

/**
 * Particle burst effect for badge earned animation.
 * Renders a Canvas with radiating particles from a center point.
 */
@Composable
fun ParticleBurst(
    modifier: Modifier = Modifier,
    color: Color = MatrixGreen,
    particleCount: Int = 24
) {
    val particles = remember {
        List(particleCount) { index ->
            ParticleDefinition(
                angle = index * (360f / particleCount),
                speed = 2.5f + (index % 3) * 1.2f,
                radius = 0f
            )
        }
    }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "particleBurst"
    )

    Box(modifier = modifier
        .drawBehind {
            particles.forEach { particle ->
                val currentRadius = particle.radius + particle.speed * 200f * animationProgress
                val x = center.x + currentRadius * cos(Math.toRadians(particle.angle.toDouble())).toFloat()
                val y = center.y + currentRadius * sin(Math.toRadians(particle.angle.toDouble())).toFloat()
                val alpha = 1f - animationProgress
                drawCircle(
                    color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = 6f,
                    center = Offset(x, y)
                )
            }
        }
    )
}

data class ParticleDefinition(
    val angle: Float,
    val speed: Float,
    val radius: Float
)
