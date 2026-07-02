package com.revrank.domain.model

import androidx.compose.ui.graphics.Color

enum class Rank(
    val level: Int,
    val displayName: String,
    val xpRequired: Int,
    val color: Color
) {
    LEARNER(1, "Learner", 0, Color(0xFF888888)),
    CRUISER(2, "Cruiser", 500, Color(0xFFFFFF00)),
    ROAD_CAPTAIN(3, "Road Captain", 1500, Color(0xFFFFA500)),
    APEX_DRIVER(4, "Apex Driver", 3500, Color(0xFF00FF00)),
    STREET_GHOST(5, "Street Ghost", 7000, Color(0xFF00FFFF)),
    MIDNIGHT_RACER(6, "Midnight Racer", 15000, Color(0xFFFF0000)),
    LEGEND(7, "Legend", 30000, Color(0xFFFF00FF));

    companion object {
        fun fromXp(xp: Int): Rank = entries.lastOrNull { xp >= it.xpRequired } ?: LEARNER

        fun progressToNext(xp: Int): Float {
            val current = fromXp(xp)
            val nextIndex = current.ordinal + 1
            if (nextIndex >= entries.size) return 1f
            val next = entries[nextIndex]
            return (xp - current.xpRequired).toFloat() / (next.xpRequired - current.xpRequired)
        }
    }
}

// Extension for grade color based on score
fun Int.toGradeColor(): Color = when {
    this >= 90 -> Color(0xFF00FF41) // Perfect
    this >= 75 -> Color(0xFFADFF2F) // Great
    this >= 55 -> Color(0xFFFFFF00) // Good
    this >= 35 -> Color(0xFFFFA500) // Okay
    else -> Color(0xFFFF0000)            // Poor
}

// Extension for grade letter
fun Int.toGrade(): String = when {
    this >= 90 -> "S"
    this >= 75 -> "A"
    this >= 55 -> "B"
    this >= 35 -> "C"
    else -> "D"
}
