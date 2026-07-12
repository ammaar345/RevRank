package com.revrank.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.Border
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.ShareTechMono
import com.revrank.presentation.theme.Surface
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.Void

@Composable
fun ScoreCategoryBar(
    label: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    val fillFraction = score / 100f
    val barColor = when {
        score >= 90 -> MatrixGreen
        score >= 75 -> com.revrank.presentation.theme.ScoreGreat
        score >= 55 -> com.revrank.presentation.theme.ScoreGood
        score >= 35 -> com.revrank.presentation.theme.ScoreOkay
        else -> com.revrank.presentation.theme.ScorePoor
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = com.revrank.presentation.theme.RevRankTypography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$score",
                style = com.revrank.presentation.theme.RevRankTypography.bodyMedium,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Void)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(fillFraction)
                    .height(4.dp)
                    .background(barColor)
            )
        }
    }
}
