package de.trbnb.darts.ui.customview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ColorPickerPreview() {
    var color by remember { mutableStateOf(Color(red = 1f, green = 0.55f, blue = 0.23f)) }
    ColorPicker(
        color = color,
        onColorChanged = { color = it }
    )
}

@Composable
fun ColorPicker(
    color: Color,
    onColorChanged: (color: Color) -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceBetween
) = Column(modifier, verticalArrangement = verticalArrangement) {
    class Colors(trackColor: Color) : SliderColors {
        private val tickColor = mutableStateOf(Color.Black)
        private val trackColor = mutableStateOf(trackColor)
        @Composable
        override fun thumbColor(enabled: Boolean) = trackColor

        @Composable
        override fun tickColor(enabled: Boolean, active: Boolean) = tickColor

        @Composable
        override fun trackColor(enabled: Boolean, active: Boolean) = trackColor
    }

    Slider(
        value = color.red,
        onValueChange = { onColorChanged(color.copy(red = it)) },
        colors = Colors(Color.Red),
        modifier = Modifier.fillMaxWidth()
    )

    Slider(
        value = color.green,
        onValueChange = { onColorChanged(color.copy(green = it)) },
        colors = Colors(Color.Green),
        modifier = Modifier.fillMaxWidth()
    )

    Slider(
        value = color.blue,
        onValueChange = { onColorChanged(color.copy(blue = it)) },
        colors = Colors(Color.Blue),
        modifier = Modifier.fillMaxWidth()
    )
}