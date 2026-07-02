package com.revrank.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

fun Modifier.scanLines(
    alpha: Float = 0.03f,
    lineSpacingPx: Float = 8f,
    color: Color = Color.Black
): Modifier = this.drawWithContent {
    drawContent()
    var y = 0f
    while (y < size.height) {
        drawLine(
            color.copy(alpha = alpha),
            Offset(0f, y),
            Offset(size.width, y),
            strokeWidth = 1f
        )
        y += lineSpacingPx
    }
}
