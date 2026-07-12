package com.revrank.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.R
import com.revrank.presentation.theme.Phosphor
import com.revrank.presentation.theme.VT323
import com.revrank.presentation.theme.Void

/**
 * Shared CRT-terminal background used across screens so they all read like the
 * sign-in baseline (designs/b00-sign-in-google.html): black void, faint matrix
 * rain, phosphor scanlines, a legibility scrim, and a `> REVRANK // SCREEN_ID`
 * terminal chrome tag. Screen content is placed on top via [content].
 *
 * Callers own their own inner padding, but should use `systemBarsPadding()` +
 * a horizontal inset (>= 24.dp) so tap targets never sit against the edges.
 */
@Composable
fun TerminalScaffold(
    screenId: String,
    modifier: Modifier = Modifier,
    showBike: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(Void)) {

        if (showBike) {
            Image(
                painter = painterResource(R.drawable.bike_hero),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                alpha = 0.10f
            )
        }

        MatrixRainBackground(
            modifier = Modifier.fillMaxSize().alpha(0.10f),
            density = 22,
            color = Phosphor,
            fontSize = 14
        )

        // Legibility scrim — darker at top/bottom, clearer in the middle.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Void.copy(alpha = 0.68f),
                        0.32f to Void.copy(alpha = 0.22f),
                        0.70f to Void.copy(alpha = 0.22f),
                        1.00f to Void.copy(alpha = 0.78f)
                    )
                )
        )

        // Phosphor scanlines.
        Box(modifier = Modifier.fillMaxSize().scanLines(alpha = 0.045f, lineSpacingPx = 8f, color = Phosphor))

        // Terminal chrome tag.
        Text(
            text = "> REVRANK // $screenId",
            fontFamily = VT323,
            fontSize = 15.sp,
            letterSpacing = 2.sp,
            color = Phosphor.copy(alpha = 0.55f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 20.dp, top = 10.dp)
        )

        content()
    }
}
