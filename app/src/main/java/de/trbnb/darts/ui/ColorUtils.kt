package de.trbnb.darts.ui

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.abs
import kotlin.math.pow

private fun computeContrastBetweenColors(bg: Int, fg: Int): Float {
    var bgR: Float = Color.red(bg) / 255f
    var bgG: Float = Color.green(bg) / 255f
    var bgB: Float = Color.blue(bg) / 255f
    bgR = if (bgR < 0.03928f) bgR / 12.92f else ((bgR + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    bgG = if (bgG < 0.03928f) bgG / 12.92f else ((bgG + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    bgB = if (bgB < 0.03928f) bgB / 12.92f else ((bgB + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    val bgL = 0.2126f * bgR + 0.7152f * bgG + 0.0722f * bgB
    var fgR: Float = Color.red(fg) / 255f
    var fgG: Float = Color.green(fg) / 255f
    var fgB: Float = Color.blue(fg) / 255f
    fgR = if (fgR < 0.03928f) fgR / 12.92f else ((fgR + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    fgG = if (fgG < 0.03928f) fgG / 12.92f else ((fgG + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    fgB = if (fgB < 0.03928f) fgB / 12.92f else ((fgB + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
    val fgL = 0.2126f * fgR + 0.7152f * fgG + 0.0722f * fgB
    return abs((fgL + 0.05f) / (bgL + 0.05f))
}

fun @receiver:ColorInt Int.useLightOnPrimaryColor(): Boolean {
    return computeContrastBetweenColors(this, Color.WHITE) > 3f
}