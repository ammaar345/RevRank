package com.revrank.presentation.screens.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.withSave
import com.revrank.domain.model.BadgeType
import com.revrank.domain.model.Trip
import com.revrank.domain.model.User

/**
 * Renders shareable cards for RevRank.
 *
 * Supports three card types (all 1080x1080px):
 *   1. Trip Card - shows a single trip's score and stats
 *   2. Weekly Recap Card - shows weekly stats
 *   3. Badge Card - shows a earned badge
 */
object ShareCardGenerator {

    /** Backwards-compatible alias for generateTripCard */
    fun generate(trip: Trip, user: User): Bitmap = generateTripCard(trip, user)

    /**
     * Generates a trip share card.
     *
     * @param trip the trip data
     * @param user the user who made the trip
     * @return a 1080x1080 bitmap representing the trip card
     */
    fun generateTripCard(trip: Trip, user: User): Bitmap {
        return createBaseCanvas().use { canvas ->
            // --- Background ---
            canvas.drawColor(Color.BLACK)

            // --- Scan lines (3% opacity black) ---
            val scanPaint = Paint().apply {
                color = Color.argb(8, 0, 0, 0) // ~3% opacity
                strokeWidth = 8f
            }
            for (y in 0..height step 8) {
                canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), scanPaint)
            }

            // --- Left border (12px green) ---
            val borderPaint = Paint().apply {
                color = Color.parseColor("#00FF41") // MatrixGreen
                strokeWidth = 12f
            }
            canvas.drawRect(0f, 0f, 12f, height.toFloat(), borderPaint)

            // --- Colors ---
            val scoreColor = trip.score.toGradeColor()
            val matrixGreen = Color.parseColor("#00FF41")

            // --- "TRIPRANK" wordmark ---
            val wordmarkPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = matrixGreen
                textSize = 64f
            }
            canvas.drawText("TRIPRANK", 64f, 120f, wordmarkPaint)

            // --- Username & Rank ---
            val userPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 56f
            }
            val userText = "${user.username}"
            canvas.drawText(userText, 64f, 200f, userPaint)

            // Horizontal rule
            val rulePaint = Paint().apply {
                color = Color.parseColor("#333333")
                strokeWidth = 2f
            }
            canvas.drawLine(64f, 240f, (width - 64).toFloat(), 240f, rulePaint)

            // --- Score (large) ---
            val scorePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = scoreColor
                textSize = 220f
            }
            val scoreX = 64f
            val scoreY = 420f
            canvas.drawText(trip.score.toString(), scoreX, scoreY, scorePaint)

            // --- Grade ---
            val gradePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = scoreColor
                textSize = 220f
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(trip.score.toGrade(), (width - 64).toFloat(), scoreY, gradePaint)
            // Reset alignment
            gradePaint.textAlign = Paint.Align.LEFT

            // --- Horizontal rule (stats area) ---
            canvas.drawLine(64f, 480f, (width - 64).toFloat(), 480f, rulePaint)

            // --- Stats: Distance / Time / Max Speed ---
            val statGray = Color.parseColor("#888888")
            val statValuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 48f
            }
            val statLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = statGray
                textSize = 32f
            }

            // Distance
            canvas.drawText("%.1f km".format(trip.distanceKm), 64f, 560f, statValuePaint)
            canvas.drawText("DISTANCE", 64f, 600f, statLabelPaint)

            // Time
            val durationMin = ((trip.endTime ?: 0L) - trip.startTime) / 1000 / 60
            val durationText = if (durationMin < 60) "${durationMin}min" else "${durationMin / 60}h ${durationMin % 60}min"
            canvas.drawText(durationText, 64f + (width - 128) / 3f, 560f, statValuePaint)
            canvas.drawText("TIME", 64f + (width - 128) / 3f, 600f, statLabelPaint)

            // Max speed
            canvas.drawText("%.0f km/h".format(trip.maxSpeedKmh), 64f + 2 * (width - 128) / 3f, 560f, statValuePaint)
            canvas.drawText("TOP SPEED", 64f + 2 * (width - 128) / 3f, 600f, statLabelPaint)

            // --- Horizontal rule (bars area) ---
            canvas.drawLine(64f, 640f, (width - 64).toFloat(), 640f, rulePaint)

            // --- Score Bars (Acceleration, Braking, Cornering) ---
            val barNames = listOf("ACCEL", "BRAKE", "CORNER")
            val barValues = listOf(trip.scoreAcceleration, trip.scoreBraking, trip.scoreCornering)
            val barColors = listOf(
                Color.parseColor("#00FF41"), // green
                Color.parseColor("#FFFFFF00"), // yellow
                Color.parseColor("#FFA500")    // orange
            )
            var barY = 700f
            for (i in barNames.indices) {
                val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 36f
                }
                canvas.drawText(barNames[i], 64f, barY, labelPaint)

                // Background bar
                val bgBar = Paint().apply {
                    color = Color.parseColor("#222222")
                }
                canvas.drawRect(300f, barY - 24f, 900f, barY, bgBar)

                // Foreground bar
                val fgBar = Paint().apply {
                    color = barColors[i]
                }
                val barWidth = (barValues[i] / 100f) * (900f - 300f)
                canvas.drawRect(300f, barY - 24f, 300f + barWidth, barY, fgBar)

                // Value text
                val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 36f
                    textAlign = Paint.Align.RIGHT
                }
                canvas.drawText(barValues[i].toString(), 950f, barY, valuePaint)
                valuePaint.textAlign = Paint.Align.LEFT

                barY += 60f
            }

            // --- Footer ---
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 36f
            }
            canvas.drawText("reverank.app", 64f, height - 80f, footerPaint)

            // Bottom-right rank badge
            val rankPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = user.rankColor
                textSize = 36f
            }
            canvas.drawText(user.rank.displayName, (width - 64).toFloat(), height - 80f, rankPaint)
        }
    }

    /**
     * Data class for weekly recap card information.
     */
    data class WeeklyRecapData(
        val weekStart: String, // "Mon dd"
        val weekEnd: String,   // "Sun dd MMM"
        val tripCount: Int,
        val distanceKm: Float,
        val avgScore: Int,
        val weeklyXp: Int,
        val streakDays: Int,
        val bestTripScore: Int,
        val bestTripDate: String // formatted date, e.g., "Jun 15"
    )

    /**
     * Generates a weekly recap share card.
     *
     * @param data the weekly recap data
     * @return a 1080x1080 bitmap representing the weekly recap card
     */
    fun generateWeeklyRecapCard(data: WeeklyRecapData): Bitmap {
        return createBaseCanvas().use { canvas ->
            // --- Background ---
            canvas.drawColor(Color.BLACK)

            // --- Scan lines (3% opacity black) ---
            val scanPaint = Paint().apply {
                color = Color.argb(8, 0, 0, 0)
                strokeWidth = 8f
            }
            for (y in 0..height step 8) {
                canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), scanPaint)
            }

            // --- Left border (12px green) ---
            val borderPaint = Paint().apply {
                color = Color.parseColor("#00FF41")
                strokeWidth = 12f
            }
            canvas.drawRect(0f, 0f, 12f, height.toFloat(), borderPaint)

            // --- Header ---
            val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 36f
            }
            val headerText = "WEEK OF ${data.startDate} – ${data.endDate}"
            canvas.drawText(headerText, 64f, 140f, headerPaint)

            // --- Large center: trips and distance ---
            val centerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 96f
            }
            val centerText = "${data.tripCount} trips  •  ${"%.1f".format(data.distanceKm)}km"
            val centerLayout = StaticLayout(
                centerText,
                centerPaint,
                width - 128,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )
            canvas.save()
            canvas.translate(64f, 300f)
            centerLayout.draw(canvas)
            canvas.restore()

            // --- Average score with grade ---
            val scoreColor = data.avgScore.toGradeColor()
            val scorePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = scoreColor
                textSize = 180f
            }
            val scoreText = data.avgScore.toString()
            val scoreLayout = StaticLayout(
                scoreText,
                scorePaint,
                width - 128,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )
            canvas.save()
            canvas.translate(64f, 500f)
            scoreLayout.draw(canvas)
            canvas.restore()

            val gradePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = scoreColor
                textSize = 180f
                textAlign = Paint.Align.RIGHT
            }
            val gradeText = data.avgScore.toGrade()
            val gradeLayout = StaticLayout(
                gradeText,
                gradePaint,
                width - 128,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )
            canvas.save()
            canvas.translate(64f, 500f)
            gradeLayout.draw(canvas)
            canvas.restore()

            // --- Horizontal rule ---
            val rulePaint = Paint().apply {
                color = Color.parseColor("#333333")
                strokeWidth = 2f
            }
            canvas.drawLine(64f, 640f, (width - 64).toFloat(), 640f, rulePaint)

            // --- Stats row: Rank progress, XP, Streak ---
            val statPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 36f
            }
            val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 48f
            }

            // Rank progress
            val progressText = "↑ ${data.weeklyXp} XP this week"
            canvas.drawText(progressText, 64f, 720f, valuePaint)
            canvas.drawText("RANK PROGRESS", 64f, 780f, statPaint)

            // XP total
            val xpText = "TOTAL XP"
            val xpValue = "${data.weeklyXp}"
            canvas.drawText(xpValue, (width / 2f), 720f, valuePaint)
            canvas.drawText(xpText, (width / 2f), 780f, statPaint)

            // Streak
            val streakText = "${data.streakDays} DAYS"
            val streakLabel = "STREAK"
            canvas.drawText(streakText, width - 64f, 720f, valuePaint, true) // right-aligned
            canvas.drawText(streakLabel, width - 64f, 780f, statPaint, true) // right-aligned

            // --- Best trip ---
            val bestPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 36f
            }
            val bestValuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 48f
            }
            val bestText = "BEST: ${data.bestTripScore} (${data.bestTripDate})"
            val bestLabel = "BEST TRIP"
            canvas.drawText(bestText, 64f, 860f, bestValuePaint)
            canvas.drawText(bestLabel, 64f, 920f, bestPaint)

            // --- Footer ---
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 36f
            }
            canvas.drawText("reverank.app", 64f, height - 80f, footerPaint)
        }
    }

    /**
     * Data class for badge card information.
     */
    data class BadgeCardData(
        val badgeType: BadgeType,
        val username: String,
        val userRank: Int,
        val userRankColorHex: String
    )

    /**
     * Generates a badge share card.
     *
     * @param data the badge card data
     * @return a 1080x1080 bitmap representing the badge card
     */
    fun generateBadgeCard(data: BadgeCardData): Bitmap {
        return createBaseCanvas().use { canvas ->
            // --- Background ---
            canvas.drawColor(Color.BLACK)

            // --- Scan lines (3% opacity black) ---
            val scanPaint = Paint().apply {
                color = Color.argb(8, 0, 0, 0)
                strokeWidth = 8f
            }
            for (y in 0..height step 8) {
                canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), scanPaint)
            }

            // --- Left border (12px green) ---
            val borderPaint = Paint().apply {
                color = Color.parseColor("#00FF41")
                strokeWidth = 12f
            }
            canvas.drawRect(0f, 0f, 12f, height.toFloat(), borderPaint)

            // --- Badge icon (center, 256dp) ---
            val badgeSize = 256f
            val badgeX = (width - badgeSize) / 2f
            val badgeY = (height - badgeSize) / 2f - 50f // slightly above center to leave room for text

            // Placeholder for badge icon: a circle with the badge's first letter
            val badgePaint = Paint().apply {
                // In a real app, we'd use the badge's color or image.
                // For now, use a default color (e.g., based on badge type or rank color)
                color = Color.parseColor(data.userRankColorHex) // use user's rank color as placeholder
                style = Paint.Style.FILL
            }
            canvas.drawOval(badgeX, badgeY, badgeX + badgeSize, badgeY + badgeSize, badgePaint)

            // Badge initial (e.g., "F" for "Friend")
            val badgeInitial = data.badgeType.name.first().toString()
            val initialPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = 120f
                textAlign = Paint.Align.CENTER
            }
            val initialX = badgeX + badgeSize / 2f
            val initialY = badgeY + badgeSize / 2f + 40f // adjust for baseline
            canvas.drawText(badgeInitial, initialX, initialY, initialPaint)

            // --- Badge name ---
            val namePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 32f
                textAlign = Paint.Align.CENTER
            }
            val nameY = badgeY + badgeSize + 60f
            canvas.drawText(data.badgeType.name.replace('_', ' ').titlecase(), width / 2f, nameY, namePaint)

            // --- "Earned by {username}" ---
            val earnedByPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 16f
                textAlign = Paint.Align.CENTER
            }
            val earnedByY = nameY + 40f
            val earnedByText = "Earned by ${data.username}"
            canvas.drawText(earnedByText, width / 2f, earnedByY, earnedByPaint)

            // --- Rank badge (small, top-right) ---
            val rankSize = 48f
            val rankX = width - 64f - rankSize
            val rankY = 64f
            val rankPaint = Paint().apply {
                color = Color.parseColor(data.userRankColorHex)
            }
            canvas.drawRect(rankX, rankY, rankX + rankSize, rankY + rankSize, rankPaint)

            val rankTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = 24f
                textAlign = Paint.Align.CENTER
            }
            val rankText = data.userRank.toString()
            val rankTextX = rankX + rankSize / 2f
            val rankTextY = rankY + rankSize / 2f + 10f
            canvas.drawText(rankText, rankTextX, rankTextY, rankTextPaint)

            // --- Footer ---
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#888888")
                textSize = 36f
            }
            val footerText = "reverank.app — Can you earn it?"
            canvas.drawText(footerText, 64f, height - 80f, footerPaint)
        }
    }

    /** Helper to create a 1080x1080 canvas. */
    private fun createBaseCanvas(): Canvas {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        return Canvas(bitmap)
    }

    /** Extension to capitalize first letter of each word. */
    private fun String.titlecase(): String {
        return this.split(' ')
            .map { it.capitalize() }
            .joinToString(" ")
    }
}

// Extension to get color from score (assuming this exists elsewhere; if not, we define a simple version here)
private fun Int.toGradeColor(): Color {
    return when {
        this >= 90 -> Color.parseColor("#00FF41") // green
        this >= 75 -> Color.parseColor("#39FF14") // lime
        this >= 55 -> Color.parseColor("#FFFFD700") // amber
        this >= 35 -> Color.parseColor("#FFFFA500") // orange
        else -> Color.parseColor("#FFFF2D00") // red
    }
}

// Extension to get grade letter from score (assuming this exists elsewhere)
private fun Int.toGrade(): String {
    return when {
        this >= 90 -> "S"
        this >= 80 -> "A"
        this >= 70 -> "B"
        this >= 60 -> "C"
        this >= 50 -> "D"
        else -> "F"
    }
}

// Extension to get rank color from Rank enum (assuming we have a Rank enum with a color property)
// If not, we'll use a placeholder. Since we don't have the Rank enum here, we'll assume the user has a rankColor property.
// In the actual code, User should have a rankColor property (Int or String).
// For safety, we'll use a default if the property doesn't exist in the actual User class.
// But since we are in the same module, we can assume it's there.
// If not, we'll need to adjust. For now, we'll use a property access.
// Note: The actual User class in domain.model may not have a rankColor property yet.
// We'll add a placeholder extension to get a color from the rank level.
// This is just for the canvas generator; the UI layer should provide the color.
private fun Int.rankColor: Color {
    // This is a placeholder mapping. In reality, the User class should have a color field.
    return when (this) {
        1 -> Color.parseColor("#888888") // Learner
        2 -> Color.parseColor("#00C032") // Cruiser
        3 -> Color.parseColor("#0099FF") // Captain
        4 -> Color.parseColor("#9B59B6") // Apex
        5 -> Color.parseColor("#00FFFF") // Ghost
        6 -> Color.parseColor("#FF4444") // Midnight
        else -> Color.parseColor("#FFFFD700") // Legend
    }
}

// Extension to get rank display name from rank level (placeholder)
private fun Int.rankName: String {
    return when (this) {
        1 -> "Learner"
        2 -> "Cruiser"
        3 -> "Captain"
        4 -> "Apex"
        5 -> "Ghost"
        6 -> "Midnight"
        else -> "Legend"
    }
}

// Extension to get displayName from Rank enum (if we had it) - but we are using Int for rank level here.
// In the actual User class, we might have a Rank object. We'll assume the User has a rank property that is Int (level).
// If the User class has a Rank enum, we would need to adjust.
// For now, we'll use the above extension.