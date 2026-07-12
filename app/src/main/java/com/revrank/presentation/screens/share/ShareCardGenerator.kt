package com.revrank.presentation.screens.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.revrank.domain.model.Trip
import com.revrank.domain.model.User

/**
 * Renders 1080x1080 share cards with the CRT terminal look:
 * black background, phosphor green accents, mono typography.
 */
object ShareCardGenerator {

    private const val SIZE = 1080f
    private const val PHOSPHOR = 0xFF00FF66.toInt()
    private const val DIM = 0xFF888888.toInt()
    private const val TRACK = 0xFF222222.toInt()

    fun generate(trip: Trip, user: User): Bitmap {
        val bitmap = Bitmap.createBitmap(SIZE.toInt(), SIZE.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.BLACK)
        drawScanlines(canvas)
        drawFrame(canvas)

        val mono = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        val monoRegular = Typeface.MONOSPACE

        // Terminal header
        canvas.drawText(
            "> REVRANK // TRIP_CARD",
            64f, 96f,
            textPaint(28f, PHOSPHOR, monoRegular).apply { alpha = 160 }
        )

        // Score — huge center
        val scoreColor = scoreColor(trip.score)
        val scorePaint = textPaint(320f, scoreColor, mono).apply {
            textAlign = Paint.Align.CENTER
            setShadowLayer(40f, 0f, 0f, scoreColor)
        }
        canvas.drawText(trip.score.toString(), SIZE / 2, 480f, scorePaint)

        val labelPaint = textPaint(36f, DIM, monoRegular).apply { textAlign = Paint.Align.CENTER }
        canvas.drawText("OVERALL SCORE", SIZE / 2, 540f, labelPaint)

        // Stats row
        val statY = 700f
        drawStat(canvas, 190f, statY, "%.1f km".format(trip.distanceKm), "DISTANCE")
        drawStat(canvas, SIZE / 2, statY, durationLabel(trip), "TIME")
        drawStat(canvas, SIZE - 190f, statY, "%.0f km/h".format(trip.maxSpeedKmh), "TOP SPEED")

        // Divider
        canvas.drawRect(64f, 800f, SIZE - 64f, 802f, fillPaint(TRACK))

        // Username
        canvas.drawText(
            "@${user.username}",
            64f, 900f,
            textPaint(44f, Color.WHITE, mono)
        )

        // Footer
        canvas.drawText(
            "reverank.app",
            SIZE - 64f, 1000f,
            textPaint(32f, PHOSPHOR, monoRegular).apply { textAlign = Paint.Align.RIGHT }
        )

        return bitmap
    }

    private fun drawStat(canvas: Canvas, x: Float, y: Float, value: String, label: String) {
        canvas.drawText(
            value, x, y,
            textPaint(52f, Color.WHITE, Typeface.MONOSPACE).apply { textAlign = Paint.Align.CENTER }
        )
        canvas.drawText(
            label, x, y + 44f,
            textPaint(26f, DIM, Typeface.MONOSPACE).apply { textAlign = Paint.Align.CENTER }
        )
    }

    private fun drawScanlines(canvas: Canvas) {
        val paint = fillPaint(PHOSPHOR).apply { alpha = 10 }
        var y = 0f
        while (y < SIZE) {
            canvas.drawRect(0f, y, SIZE, y + 2f, paint)
            y += 8f
        }
    }

    private fun drawFrame(canvas: Canvas) {
        val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = PHOSPHOR
            alpha = 90
        }
        canvas.drawRect(24f, 24f, SIZE - 24f, SIZE - 24f, border)
    }

    private fun textPaint(size: Float, colorInt: Int, typeface: Typeface): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            color = colorInt
            this.typeface = typeface
            letterSpacing = 0.08f
        }

    private fun fillPaint(colorInt: Int): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorInt
        }

    private fun durationLabel(trip: Trip): String {
        val minutes = ((trip.endTime ?: trip.startTime) - trip.startTime) / 1000 / 60
        return "${minutes}min"
    }

    private fun scoreColor(score: Int): Int = when {
        score >= 90 -> 0xFF00FF41.toInt()
        score >= 75 -> 0xFFADFF2F.toInt()
        score >= 55 -> 0xFFFFD700.toInt()
        score >= 35 -> 0xFFFF6B00.toInt()
        else -> 0xFFFF453A.toInt()
    }
}
