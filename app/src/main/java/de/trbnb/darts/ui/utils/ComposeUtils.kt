package de.trbnb.darts.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

val Dp.px: Float
    @Composable
    get() = LocalDensity.current.run { toPx() }