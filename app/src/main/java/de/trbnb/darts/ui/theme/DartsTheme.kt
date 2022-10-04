package de.trbnb.darts.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import de.trbnb.darts.ui.match.BoardColors

@Composable
fun DartsTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkMode) darkColors(
            primary = BoardColors.red,
            secondary = BoardColors.green
        ) else lightColors(
            primary = BoardColors.red,
            secondary = BoardColors.green
        )
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.onBackground
        ) {
            content()
        }
    }
}