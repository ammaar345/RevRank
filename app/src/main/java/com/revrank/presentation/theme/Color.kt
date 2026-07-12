package com.revrank.presentation.theme

import androidx.compose.ui.graphics.Color

// Backgrounds
val Void = Color(0xFF000000)
val Surface = Color(0xFF0A0A0A)
val Surface2 = Color(0xFF111111)
val Surface3 = Color(0xFF1A1A1A)

// Phosphor Green Family (primary accent — matches CRT terminal design baseline)
// Design ruling (2026-07-11): #00FF66 phosphor is THE accent; score-green #00FF41
// below remains the 90+ semantic score color only.
val MatrixGreen = Color(0xFF00FF66)   // primary accent (was #00FF41)
val MatrixDim = Color(0xFF00CC44)
val MatrixMuted = Color(0xFF008833)
val MatrixGlow = Color(0x3300FF66)
// Explicit phosphor aliases for new code
val Phosphor = Color(0xFF00FF66)
val PhosphorDim = Color(0xFF00CC44)
val PhosphorDark = Color(0xFF003D1A)

// Score / Semantic Colors
val ScorePerfect = Color(0xFF00FF41)
val ScoreGreat = Color(0xFF39FF14)
val ScoreGood = Color(0xFFFFD700)
val ScoreOkay = Color(0xFFFF6B00)
val ScorePoor = Color(0xFFFF2D00)

// Rank Colors
val RankLearner = Color(0xFF888888)
val RankCruiser = Color(0xFF00C032)
val RankCaptain = Color(0xFF0099FF)
val RankApex = Color(0xFF9B59B6)
val RankGhost = Color(0xFF00FFFF)
val RankMidnight = Color(0xFFFF4444)
val RankLegend = Color(0xFFFFD700)

// UI Neutrals
val TextPrimary = Color(0xFFE8E8E8)
val TextSecondary = Color(0xFF888888)
val TextDim = Color(0xFF444444)
val Border = Color(0xFF1E1E1E)

// Alert Colors
val Danger = Color(0xFFFF453A)
val Warning = Color(0xFFFF8C00)
val Success = Color(0xFF00FF66)
val Info = Color(0xFF00B4D8)
