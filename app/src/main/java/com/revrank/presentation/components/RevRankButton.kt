package com.revrank.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.Border
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Void

@Composable
fun RevRankButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Void,
            contentColor = MatrixGreen
        ),
        border = BorderStroke(1.dp, MatrixGreen),
        shape = ButtonDefaults.outlinedShape
    ) {
        Text(
            text = text.uppercase(),
            style = com.revrank.presentation.theme.RevRankTypography.labelSmall
        )
    }
}
