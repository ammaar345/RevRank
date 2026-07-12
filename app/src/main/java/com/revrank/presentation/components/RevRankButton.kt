package com.revrank.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.JetBrainsMono
import com.revrank.presentation.theme.Phosphor
import com.revrank.presentation.theme.Void

/**
 * The one CRT terminal button used everywhere — sharp 2px rectangle, mono
 * uppercase, tracked-out label. Identical tap feel across the whole app:
 *
 *  - press → scales to 0.96 (spring) on every button
 *  - ghost (primary=false): transparent + phosphor border, and INVERTS to a
 *    solid phosphor fill with black label while pressed (mirrors the sign-in
 *    baseline's hover-invert)
 *  - primary=true: solid phosphor fill, black label at rest
 *
 * @param leadingIcon optional glyph (e.g. the Google G) tinted to match the label
 * @param showCursor  appends a blinking block cursor for hero CTAs
 */
@Composable
fun RevRankButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false,
    leadingIcon: ImageVector? = null,
    showCursor: Boolean = false
) {
    val shape = RoundedCornerShape(2.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    // A ghost button inverts to a fill while pressed; a primary button is always filled.
    val filled = primary || (pressed && enabled)
    val bg = when {
        !enabled && primary -> Phosphor.copy(alpha = 0.3f)
        filled -> Phosphor
        else -> Color.Transparent
    }
    val contentColor = when {
        !enabled -> if (primary) Void else Phosphor.copy(alpha = 0.35f)
        filled -> Void
        else -> Phosphor
    }
    val borderColor = if (enabled) Phosphor else Phosphor.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(bg, shape)
            .then(if (!primary) Modifier.border(BorderStroke(1.5.dp, borderColor), shape) else Modifier)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            leadingIcon?.let {
                Icon(imageVector = it, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            }
            Text(
                text = text.uppercase(),
                style = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 3.sp
                ),
                color = contentColor
            )
            if (showCursor) {
                Text(
                    text = "█",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = blinkAlpha())
                )
            }
        }
    }
}

/** Hard on/off block-cursor blink shared by terminal CTAs. */
@Composable
fun blinkAlpha(): Float {
    val infinite = rememberInfiniteTransition(label = "cursor")
    val a by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                1f at 0
                1f at 449
                0f at 450
                0f at 899
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "cursorAlpha"
    )
    return a
}
