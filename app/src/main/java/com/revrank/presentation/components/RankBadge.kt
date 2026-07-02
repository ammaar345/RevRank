package com.revrank.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revrank.domain.model.Rank
import com.revrank.presentation.theme.ShareTechMono

/**
 * Square rank badge with optional shimmer for LEGEND tier.
 * Used in Home, Profile, Leaderboard, and Share cards.
 */
@Composable
fun RankBadge(
    rank: Rank,
    size: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(rank.color.copy(alpha = 0.15f))
            .border(width = 1.5.dp, color = rank.color)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Using first letter of rank name as icon placeholder
        Textuname.compose.material.MaterialText(
            text = rank.displayName.first().toString(),
            fontSize = (size.value * 0.4).sp,
            fontFamily = ShareTechMono,
            color = rank.color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun RankBadgePreview() {
    RankBadge(rank = Rank.APEX_DRIVER, size = 48.dp)
}
