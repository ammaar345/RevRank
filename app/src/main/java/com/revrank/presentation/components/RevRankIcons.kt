package com.revrank.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * RevRank custom icon set — cyberpunk HUD line icons.
 *
 * Geometry mirrors the inline SVG icons in the design prototypes (designs folder):
 * 24x24 viewport, 1.5 stroke, round caps and joins. Paths are drawn white and
 * recolored through Icon(tint = ...), matching how the HTML uses currentColor.
 */
object RevRankIcons {

    private fun hudIcon(
        name: String,
        strokes: List<String>,
        fills: List<String> = emptyList()
    ): ImageVector {
        val builder = ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        )
        strokes.forEach { d ->
            builder.addPath(
                pathData = addPathNodes(d),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
        fills.forEach { d ->
            builder.addPath(
                pathData = addPathNodes(d),
                fill = SolidColor(Color.White)
            )
        }
        return builder.build()
    }

    /** Bottom nav — Track tab: radar circle with crosshair ticks and center dot. */
    val Radar: ImageVector by lazy {
        hudIcon(
            "RevRank.Radar",
            strokes = listOf(
                "M12 4a8 8 0 1 0 0 16a8 8 0 1 0 0-16",
                "M12 2v4M12 18v4M2 12h4M18 12h4"
            ),
            fills = listOf("M12 10a2 2 0 1 0 0 4a2 2 0 1 0 0-4")
        )
    }

    /** Bottom nav — History tab: HUD clock. */
    val History: ImageVector by lazy {
        hudIcon(
            "RevRank.History",
            strokes = listOf(
                "M12 4a8 8 0 1 0 0 16a8 8 0 1 0 0-16",
                "M12 6v6l4 2"
            )
        )
    }

    /** Bottom nav — Rank tab: angular trophy cup. */
    val Trophy: ImageVector by lazy {
        hudIcon(
            "RevRank.Trophy",
            strokes = listOf(
                "M6 9H4a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2h2",
                "M18 9h2a2 2 0 0 0 2 -2V5a2 2 0 0 0 -2 -2h-2",
                "M6 3h12v7a6 6 0 0 1 -12 0z",
                "M12 16v5",
                "M8 21h8"
            )
        )
    }

    /** Bottom nav — Profile tab: user silhouette. */
    val User: ImageVector by lazy {
        hudIcon(
            "RevRank.User",
            strokes = listOf(
                "M20 21v-2a4 4 0 0 0 -4 -4H8a4 4 0 0 0 -4 4v2",
                "M12 3a4 4 0 1 0 0 8a4 4 0 1 0 0-8"
            )
        )
    }

    /** Pro gate: hexagonal digital lock with keyhole. */
    val HexLock: ImageVector by lazy {
        hudIcon(
            "RevRank.HexLock",
            strokes = listOf(
                "M12 3l8 4v10l-8 4l-8 -4V7z",
                "M9 9V7a3 3 0 0 1 6 0v2",
                "M12 10.6a1.4 1.4 0 1 0 0 2.8a1.4 1.4 0 1 0 0-2.8"
            ),
            fills = listOf("M11.4 13.2h1.2v2.2h-1.2z")
        )
    }

    /** Streak: digital flame. */
    val Flame: ImageVector by lazy {
        hudIcon(
            "RevRank.Flame",
            strokes = listOf(
                "M12 2S8 8 8 12a4 4 0 0 0 8 0c0 -4 -4 -10 -4 -10z",
                "M12 16c-2 0 -3 -1 -3 -3c0 -2 3 -5 3 -5s3 3 3 5c0 2 -1 3 -3 3z"
            )
        )
    }

    /** Badge: star outline. */
    val Star: ImageVector by lazy {
        hudIcon(
            "RevRank.Star",
            strokes = listOf(
                "M12 2l3.09 6.26L22 9.27l-5 4.87l1.18 6.88L12 17.77l-6.18 3.25L7 14.14L2 9.27l6.91 -1.01L12 2z"
            )
        )
    }

    /** Badge: lightning bolt. */
    val Bolt: ImageVector by lazy {
        hudIcon(
            "RevRank.Bolt",
            strokes = listOf("M13 2L3 14h9l-1 8l10 -12h-9l1 -8z")
        )
    }

    /** Paywall / empty badges: faceted diamond. */
    val Diamond: ImageVector by lazy {
        hudIcon(
            "RevRank.Diamond",
            strokes = listOf(
                "M12 2L2 12l10 10l10 -10L12 2z",
                "M2 12h20",
                "M7 7l5 15l5 -15"
            )
        )
    }

    /** Empty trips: route between two waypoint nodes. */
    val Route: ImageVector by lazy {
        hudIcon(
            "RevRank.Route",
            strokes = listOf(
                "M6 15.8a2.2 2.2 0 1 0 0 4.4a2.2 2.2 0 1 0 0-4.4",
                "M18 3.8a2.2 2.2 0 1 0 0 4.4a2.2 2.2 0 1 0 0-4.4",
                "M6 15.8V9a3 3 0 0 1 3 -3h6.7"
            )
        )
    }

    /** Notifications: angular bell with alert dot. */
    val Bell: ImageVector by lazy {
        hudIcon(
            "RevRank.Bell",
            strokes = listOf(
                "M18 8a6 6 0 0 0 -12 0c0 7 -3 9 -3 9h18s-3 -2 -3 -9",
                "M13.7 21a2 2 0 0 1 -3.4 0"
            ),
            fills = listOf("M17.5 3a2 2 0 1 0 0 4a2 2 0 1 0 0-4")
        )
    }

    /** Location permission: crosshair pin. */
    val CrosshairPin: ImageVector by lazy {
        hudIcon(
            "RevRank.CrosshairPin",
            strokes = listOf(
                "M12 6a6 6 0 1 0 0 12a6 6 0 1 0 0-12",
                "M12 2v4M12 18v4M2 12h4M18 12h4"
            ),
            fills = listOf("M12 10.5a1.5 1.5 0 1 0 0 3a1.5 1.5 0 1 0 0-3")
        )
    }
}
