package com.revrank.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// CRT terminal geometry — sharp, no pills (design baseline 2026-07-11)
val RevRankShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),     // buttons, chips
    small = RoundedCornerShape(2.dp),          // cards
    medium = RoundedCornerShape(4.dp),         // modals, badges
    large = RoundedCornerShape(4.dp),
    extraLarge = RoundedCornerShape(4.dp)
)
