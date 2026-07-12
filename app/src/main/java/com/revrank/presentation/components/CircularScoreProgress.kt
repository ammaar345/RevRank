package com.revrank.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.ShareTechMono

/**
 * Neon score ring with the score number centered — Compose port of the
 * SVG score radial in the design prototypes (sharp caps, phosphor glow).
 */
@Composable
fun CircularScoreProgress(
    score: Int,
    size: Dp = 240.dp,
    strokeWidth: Dp = 8.dp,
    glowColor: Color = Color(0xFF00FF66),
    trackColor: Color = Color(0x0FFFFFFF),
    durationMs: Int = 1200,
    modifier: Modifier = Modifier
) {
    val sweep by animateFloatAsState(
        targetValue = (score.coerceIn(0, 100) / 100f) * 360f,
        animationSpec = tween(durationMillis = durationMs),
        label = "scoreSweep"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            drawArc(
                color = glowColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
        }
        Text(
            text = score.toString(),
            fontSize = (size.value * 0.23f).sp,
            fontFamily = ShareTechMono,
            color = glowColor
        )
    }
}
