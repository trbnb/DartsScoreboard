package de.trbnb.darts.domain.vibration

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import de.trbnb.darts.domain.utils.wontHappen
import javax.inject.Inject

class VibratorImpl @Inject constructor(@ApplicationContext context: Context) : Vibrator {
    private val vibrationManager = context.getSystemService<android.os.Vibrator>() ?: wontHappen()
    @SuppressLint("MissingPermission")
    override fun vibrateShortly() {
        vibrationManager.vibrate(VibrationEffect.createOneShot(200, 100))
    }
}