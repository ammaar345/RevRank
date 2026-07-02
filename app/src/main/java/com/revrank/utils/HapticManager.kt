package com.revrank.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun tripStart() = vibrate(
        VibrationEffect.createOneShot(80, 180)
    )

    fun tripEnd() = vibrate(
        VibrationEffect.createWaveform(longArrayOf(0, 60, 40, 60), -1)
    )

    fun rankUp() = vibrate(
        VibrationEffect.createWaveform(longArrayOf(0, 80, 50, 80, 50, 120), -1)
    )

    fun badgeUnlock() = vibrate(
        VibrationEffect.createOneShot(120, 220)
    )

    fun scoreReveal() = vibrate(
        VibrationEffect.createOneShot(40, 100)
    )

    fun buttonPress() = vibrate(
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    )

    fun error() = vibrate(
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
    )

    private fun vibrate(effect: VibrationEffect) {
        vibrator.vibrate(effect)
    }
}
