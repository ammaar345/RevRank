package com.revrank.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.ShareTechMono
import kotlin.random.Random

private class RainColumnData(
    val length: Int,
    val speed: Float,
    val chars: List<Char>,
    val startOffset: Int
)

@Composable
fun MatrixRainBackground(
    modifier: Modifier = Modifier,
    density: Int = 20,
    color: Color = MatrixGreen,
    fontSize: Int = 14
) {
    val textMeasurer = rememberTextMeasurer()
    val charPool = remember { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%<>".toList() }

    val columns = remember {
        List(density) {
            val len = Random.nextInt(5, 15)
            RainColumnData(
                length = len,
                speed = Random.nextFloat() * 1.2f + 0.8f,
                chars = List(len) { charPool.random() },
                startOffset = Random.nextInt(2000)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "matrixRain")

    val offsets = columns.mapIndexed { colIndex, col ->
        infiniteTransition.animateFloat(
                initialValue = -200f,
                targetValue = 2000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (1200 / col.speed).toInt(),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(col.startOffset)
                ),
                label = "rainCol$colIndex"
        )
    }

    val currentOffsets = offsets.map { it.value }

    Canvas(modifier = modifier.fillMaxSize()) {
        val charHeight = fontSize.sp.toPx()
        val colSpacing = size.width / density

        currentOffsets.forEachIndexed { index, yBase ->
            val column = columns[index]
            val xPos = (index + 0.5f) * colSpacing

            column.chars.forEachIndexed { i, char ->
                val yPos = yBase - (column.length - i) * charHeight
                if (yPos in -charHeight..size.height + charHeight) {
                    val alpha = when {
                        i == column.length - 1 -> 0.9f  // head
                        i > column.length - 4 -> 0.6f    // near head
                        else -> 0.15f                     // tail
                    }
                    drawMatrixChar(
                        textMeasurer = textMeasurer,
                        char = char,
                        x = xPos,
                        y = yPos,
                        color = color.copy(alpha = alpha),
                        fontSize = fontSize.sp
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawMatrixChar(
    textMeasurer: TextMeasurer,
    char: Char,
    x: Float,
    y: Float,
    color: Color,
    fontSize: TextUnit
) {
    val textResult = textMeasurer.measure(
        text = char.toString(),
        style = TextStyle(
            fontFamily = ShareTechMono,
            fontSize = fontSize,
            color = color
        )
    )
    drawText(
        textLayoutResult = textResult,
        topLeft = Offset(x - textResult.size.width / 2f, y)
    )
}
