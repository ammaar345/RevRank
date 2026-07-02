package com.revrank.presentation.screens.score

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.ShareTechMono
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * MatrixRain: Animated random ASCII chars falling and resolving to a target digit.
 * Reusable: you provide the target digit, size, and color.
 */
@Composable
fun MatrixRainDigit(
    targetDigit: Char,
    size: Int = 64,
    color: Color = MatrixGreen,
    delayMillis: Int = 0
) {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val random = Random(min(42, delayMillis + 1))

    var currentChar by remember { mutableStateOf(chars.random(random)) }
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        // Simulate random falling
        for (i in 0..15) {
            currentChar = chars.random(random)
            delay(40)
        }
        revealed = true
        currentChar = targetDigit
    }

    Text(
        text = if (revealed) targetDigit.toString() else currentChar.toString(),
        color = if (revealed) color else color.copy(alpha = 0.6f),
        fontSize = size.sp,
        fontFamily = ShareTechMono,
        modifier = Modifier
    )
}

/**
 * Full MatrixRain grid with multiple falling columns.
 * @param text  The final text to reveal (e.g., "84")
 */
@Composable
fun MatrixRainReveal(text: String, color: Color = MatrixGreen) {
    // Phase control
    var phase by remember { mutableIntStateOf(0) } // 0 = rain, 1 = reveal
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            delay(800)
            phase = 1
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.forEachIndexed { index, char ->
            MatrixRainDigit(
                targetDigit = char,
                size = if (index == 0) 120 else 96,
                color = color,
                delayMillis = 80 * index
            )
            if (index < text.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
