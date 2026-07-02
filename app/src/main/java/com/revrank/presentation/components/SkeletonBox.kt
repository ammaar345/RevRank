package com.revrank.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.Surface2
import com.revrank.presentation.theme.Surface3

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    val shimmerColors = listOf(Surface2, Surface3, Surface2)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x - 400f, 0f),
                    end = Offset(x, 0f)
                )
            )
    )
}

@Composable
fun SkeletonRow(
    modifier: Modifier = Modifier,
    height: Dp = 72.dp
) {
    SkeletonBox(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
fun SkeletonChart(
    modifier: Modifier = Modifier,
    height: Dp = 200.dp
) {
    SkeletonBox(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(12.dp)
    )
}
