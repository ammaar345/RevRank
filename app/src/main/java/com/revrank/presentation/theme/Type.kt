@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.revrank.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.revrank.R

/**
 * Font families matching the CRT terminal design baseline
 * (designs/b00-sign-in-google.html):
 *   JetBrains Mono — numbers, scores, HUD labels, buttons
 *   VT323          — terminal chrome, boot text, status lines
 *   Orbitron       — display / hero text (logo, rank-up titles)
 *   Rajdhani       — body copy
 */

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

val VT323 = FontFamily(
    Font(R.font.vt323_regular, FontWeight.Normal)
)

// Orbitron ships as a single variable TTF; pull weight instances via FontVariation.
val Orbitron = FontFamily(
    Font(
        R.font.orbitron_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.orbitron_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.orbitron_variable,
        weight = FontWeight.Black,
        variationSettings = FontVariation.Settings(FontVariation.weight(900))
    )
)

val Rajdhani = FontFamily(
    Font(R.font.rajdhani_light, FontWeight.Light),
    Font(R.font.rajdhani_regular, FontWeight.Normal),
    Font(R.font.rajdhani_medium, FontWeight.Medium),
    Font(R.font.rajdhani_semibold, FontWeight.SemiBold),
    Font(R.font.rajdhani_bold, FontWeight.Bold)
)

/**
 * Backward-compatible alias. Screens across the app reference `ShareTechMono`
 * for their mono/HUD text; the design baseline uses JetBrains Mono, so this
 * alias re-points every existing usage without touching each call site.
 */
val ShareTechMono = JetBrainsMono

val RevRankTypography = Typography(
    // Hero / display — Orbitron
    displayLarge = TextStyle(
        fontFamily = Orbitron,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = 6.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Orbitron,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        letterSpacing = 4.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Orbitron,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 3.sp
    ),
    // Titles — JetBrains Mono
    headlineLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontSize = 15.sp
    ),
    // Body — Rajdhani
    bodyLarge = TextStyle(
        fontFamily = Rajdhani,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Rajdhani,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Labels — JetBrains Mono, tracked out like HUD readouts
    labelLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 2.sp
    )
)
