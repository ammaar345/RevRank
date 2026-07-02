package com.revrank.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.revrank.R

val ShareTechMono = FontFamily(
    Font(R.font.share_tech_mono_regular, FontWeight.Normal)
)

val Rajdhani = FontFamily(
    Font(R.font.rajdhani_regular, FontWeight.Normal),
    Font(R.font.rajdhani_medium, FontWeight.Medium),
    Font(R.font.rajdhani_semibold, FontWeight.SemiBold)
)

val RevRankTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 96.sp,
        lineHeight = 96.sp
    ),
    displayMedium = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 64.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 40.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = ShareTechMono,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Rajdhani,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Rajdhani,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Rajdhani,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 2.sp
    )
)
