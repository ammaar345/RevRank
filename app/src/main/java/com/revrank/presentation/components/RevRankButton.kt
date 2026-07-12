package com.revrank.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.presentation.theme.JetBrainsMono
import com.revrank.presentation.theme.Phosphor
import com.revrank.presentation.theme.Void

/**
 * CRT terminal button — sharp 2px rectangle (no pills), mono uppercase,
 * tracked-out label. Matches the sign-in design baseline.
 *
 * @param primary when true, phosphor-filled (black text); otherwise a ghost
 *                button (transparent + phosphor border, phosphor text).
 */
@Composable
fun RevRankButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false
) {
    val shape = RoundedCornerShape(2.dp)
    val label = @Composable {
        Text(
            text = text.uppercase(),
            style = TextStyle(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                letterSpacing = 3.sp
            )
        )
    }

    if (primary) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(50.dp),
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Phosphor,
                contentColor = Void,
                disabledContainerColor = Phosphor.copy(alpha = 0.3f)
            )
        ) { label() }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().height(50.dp),
            enabled = enabled,
            shape = shape,
            border = BorderStroke(1.5.dp, if (enabled) Phosphor else Phosphor.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Phosphor
            )
        ) { label() }
    }
}
