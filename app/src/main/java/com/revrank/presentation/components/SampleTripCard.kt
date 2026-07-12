package com.revrank.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revrank.presentation.theme.Border
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Surface
import com.revrank.presentation.theme.TextPrimary
import com.revrank.presentation.theme.TextSecondary
import com.revrank.presentation.theme.Void

@Composable
fun SampleTripCard(
    score: Int,
    grade: String,
    modifier: Modifier = Modifier
) {
    val hardcodedCategories = listOf(
        "Acceleration" to 88,
        "Braking" to 79,
        "Cornering" to 85,
        "Smoothness" to 77,
        "Consistency" to 91
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(16.dp)
    ) {
        Text(
            text = "KNOW HOW YOU DRIVE.",
            style = com.revrank.presentation.theme.RevRankTypography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$score",
                    style = com.revrank.presentation.theme.RevRankTypography.displaySmall,
                    color = MatrixGreen
                )
                Text(
                    text = "Score",
                    style = com.revrank.presentation.theme.RevRankTypography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text = grade,
                style = com.revrank.presentation.theme.RevRankTypography.displayMedium,
                color = MatrixGreen
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        hardcodedCategories.forEach { (label, catScore) ->
            ScoreCategoryBar(label = label, score = catScore)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        SampleBadgeChip()
    }
}

@Composable
fun SampleBadgeChip() {
    Row {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .background(MatrixGreen, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "ALL FREE",
                style = com.revrank.presentation.theme.RevRankTypography.labelSmall,
                color = Void
            )
        }
    }
}
