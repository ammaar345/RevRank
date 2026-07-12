package com.revrank.presentation.screens.ranks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Rank
import com.revrank.presentation.components.RankBadge
import com.revrank.presentation.theme.MatrixGreen
import com.revrank.presentation.theme.Rajdhani
import com.revrank.presentation.theme.ShareTechMono

/**
 * RanksScreen — rank ladder, badge grid, badge detail sheet.
 */
@Composable
fun RanksScreen(
    currentRank: Rank,
    xp: Int,
    badges: List<BadgeType>,
    onBadgeTap: (BadgeType) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ─── CURRENT RANK CARD ───
        CurrentRankCard(rank = currentRank, xp = xp)
        Spacer(modifier = Modifier.height(20.dp))

        // ─── RANK LADDER ───
        Text(
            text = "RANK LADDER",
            fontSize = 11.sp,
            color = Color(0xFF888888),
            fontFamily = Rajdhani,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Rank.entries.forEach { rank ->
            RankLadderItem(rank = rank, isCurrent = rank == currentRank, isCompleted = rank.ordinal < currentRank.ordinal)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─── BADGES HEADER ───
        Row {
            Text(
                text = "YOUR BADGES",
                fontSize = 11.sp,
                color = Color(0xFF888888),
                fontFamily = Rajdhani,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${badges.size}/16",
                fontSize = 11.sp,
                color = Color(0xFF888888),
                fontFamily = ShareTechMono
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // ─── BADGE GRID ───
        BadgeGrid(badges = badges, onTap = onBadgeTap)
    }
}

@Composable
private fun CurrentRankCard(rank: Rank, xp: Int) {
    val nextRank = Rank.entries.getOrNull(rank.ordinal + 1)
    val progress = Rank.progressToNext(xp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111111))
            .border(1.dp, Color(0xFF333333))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RankBadge(rank = rank, size = 56.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = rank.displayName.uppercase(), fontSize = 22.sp, fontFamily = ShareTechMono, color = rank.color)
                    Text(text = "Rank ${rank.level} of 7", fontSize = 12.sp, color = Color(0xFF888888), fontFamily = Rajdhani)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            // XP bar
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF222222))) {
                Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(MatrixGreen))
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (nextRank != null) {
                Text(text = "$xp / ${nextRank.xpRequired} XP · ${nextRank.xpRequired - xp} to ${nextRank.displayName}", fontSize = 11.sp, color = Color(0xFF888888), fontFamily = ShareTechMono)
            } else {
                Text(text = "MAX RANK", fontSize = 11.sp, color = MatrixGreen, fontFamily = ShareTechMono)
            }
        }
    }
}

@Composable
private fun RankLadderItem(rank: Rank, isCurrent: Boolean, isCompleted: Boolean) {
    val textColor = when {
        isCurrent -> rank.color
        isCompleted -> Color(0xFF888888)
        else -> Color(0xFF444444)
    }
    val icon = when {
        isCurrent -> "►"
        isCompleted -> "✓"
        else -> "○"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                when {
                    isCurrent -> rank.color.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isCurrent) 1.5.dp else 0.dp,
                color = if (isCurrent) rank.color else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = icon,
            fontSize = 14.sp,
            color = textColor,
            fontFamily = ShareTechMono
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = rank.displayName,
            fontSize = 14.sp,
            color = textColor,
            fontFamily = Rajdhani
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!isCurrent) {
            Text(
                text = "${rank.xpRequired}",
                fontSize = 12.sp,
                color = textColor,
                fontFamily = ShareTechMono
            )
        }
    }
}

@Composable
private fun BadgeGrid(badges: List<BadgeType>, onTap: (BadgeType) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(badges.size) { index ->
            val badge = badges[index]
            BadgeItem(
                badge = badge,
                isEarned = true, // In real app, check against user's earned badges
                onTap = { onTap(badge) }
            )
        }
    }
}

@Composable
private fun BadgeItem(badge: BadgeType, isEarned: Boolean, onTap: () -> Unit) {
    Column(
        modifier = Modifier
            .size(64.dp)
            .background(
                if (isEarned) Color.Transparent else Color(0xFF111111)
            )
            .border(
                width = if (isEarned) 1.5.dp else 1.dp,
                color = if (isEarned) badge.color else Color(0xFF333333),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .clickable { onTap() }
    ) {
        // Icon placeholder - in real app would use badge.iconRes
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isEarned) badge.color.copy(alpha = 0.2f) else Color.Transparent
                )
                .border(
                    width = 1.dp,
                    color = if (isEarned) badge.color else Color(0xFF555555),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                )
        ) {
            Text(
                text = badge.name.take(1).uppercase(),
                fontSize = 18.sp,
                color = if (isEarned) badge.color else Color(0xFF888888),
                fontFamily = ShareTechMono,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = badge.name,
            fontSize = 9.sp,
            color = if (isEarned) Color.White else Color(0xFF888888),
            fontFamily = Rajdhani,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

// Badge Detail Sheet (bottom sheet)
@Composable
fun BadgeDetailSheet(
    badge: BadgeType,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (!isVisible) return
    // NOTE: the guard above is safe only because this composable is called at a
    // FIXED position in its parent (never in a list/branch that also emits other
    // content), so the early return doesn't shift sibling group keys.

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x000000).copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .background(Color(0xFF111111))
                .border(1.dp, Color(0xFF333333), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(24.dp)
                .width(300.dp)
                .height(200.dp)
                .clickable { } // Prevent dismiss when clicking inside
        ) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                // Badge icon (placeholder)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(badge.color.copy(alpha = 0.2f))
                        .border(1.dp, badge.color)
                ) {
                    Text(
                        text = badge.name.take(1).uppercase(),
                        fontSize = 28.sp,
                        color = badge.color,
                        fontFamily = ShareTechMono,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = badge.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = ShareTechMono
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = badge.description,
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    fontFamily = Rajdhani,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tap to dismiss",
                    fontSize = 10.sp,
                    color = Color(0xFF555555),
                    fontFamily = Rajdhani
                )
            }
        }
    }
}

@Preview
@Composable
private fun RanksScreenPreview() {
    RanksScreen(
        currentRank = Rank.APEX_DRIVER,
        xp = 2000,
        badges = listOf(
            BadgeType.PERFECT_RUN,
            BadgeType.NIGHT_RIDER,
            BadgeType.WEEK_WARRIOR
        )
    )
}