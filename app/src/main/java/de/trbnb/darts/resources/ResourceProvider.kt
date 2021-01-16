package de.trbnb.darts.resources

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

interface ResourceProvider {
    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int
}
