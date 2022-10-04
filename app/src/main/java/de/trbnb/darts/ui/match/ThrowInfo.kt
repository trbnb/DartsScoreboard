package de.trbnb.darts.ui.match

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.trbnb.darts.models.Field
import de.trbnb.darts.models.Multiplier
import de.trbnb.darts.models.Throw
import de.trbnb.darts.models.ThrowNumber
import de.trbnb.darts.models.ThrowState
import de.trbnb.darts.models.value
import de.trbnb.darts.utils.Triple
import de.trbnb.darts.utils.forEach
import de.trbnb.darts.utils.map
import kotlinx.coroutines.delay

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun ThrowInfoPreview() {
    var enabledState by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            enabledState = !enabledState
        }
    }
    ThrowInfoRow(
        throwInfos = ThrowNumber.tripple.map { number ->
            MatchViewModel.ThrowInfo(
                number,
                isNextThrow = number == ThrowNumber.TWO,
                _throw = Throw(
                    Field.TWENTY,
                    Multiplier.DOUBLE,
                    if (number == ThrowNumber.THREE) ThrowState.FALLEN_OFF else ThrowState.OK
                )
            )
        },
        currentTurnValue = 62,
        isConfirmTurnAvailable = enabledState,
        {_, _ ->}, {}, {}
    )
}

@Composable
fun ThrowInfoRow(
    uiState: MatchViewModel.UiState,
    onFallenOffChanged: (ThrowNumber, Boolean) -> Unit,
    onDeleteThrow: (ThrowNumber) -> Unit,
    onConfirmTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    ThrowInfoRow(
        throwInfos = uiState.throwInfos,
        currentTurnValue = uiState.currentTurn.value,
        isConfirmTurnAvailable = uiState.isConfirmTurnAvailable,
        onFallenOffChanged = onFallenOffChanged,
        onDeleteThrow = onDeleteThrow,
        onConfirmTurn = onConfirmTurn,
        modifier = modifier
    )
}

@Composable
fun ThrowInfoRow(
    throwInfos: Triple<MatchViewModel.ThrowInfo>,
    currentTurnValue: Int,
    isConfirmTurnAvailable: Boolean,
    onFallenOffChanged: (ThrowNumber, Boolean) -> Unit,
    onDeleteThrow: (ThrowNumber) -> Unit,
    onConfirmTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        throwInfos.forEach { throwInfo ->
            ThrowInfoColumn(
                throwInfo = throwInfo,
                onFallenOffChanged = { onFallenOffChanged(throwInfo.number, it) },
                onDelete = { onDeleteThrow(throwInfo.number) },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

@Composable
fun ThrowInfoColumn(
    throwInfo: MatchViewModel.ThrowInfo,
    onFallenOffChanged: (fallenOff: Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .then(
                when {
                    throwInfo.isNextThrow -> Modifier.background(
                        Color(0x08000000),
                        RoundedCornerShape(8.dp)
                    )
                    else -> Modifier
                }
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Wurf ${throwInfo.number.number}".uppercase(),
            style = MaterialTheme.typography.overline,
            fontSize = 13.sp
        )
        Text(
            throwInfo.value,
            style = MaterialTheme.typography.subtitle1.copy(
                textDecoration = when {
                    throwInfo.isFallenOff -> TextDecoration.LineThrough
                    else -> TextDecoration.None
                }
            ),
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            throwInfo.description,
            style = MaterialTheme.typography.caption,
            fontSize = 15.sp
        )

        AnimatedVisibility(
            throwInfo._throw != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier.fillMaxWidth(0.8f).padding(top = 8.dp)
        ) {
            Row {
                Checkbox(
                    checked = !throwInfo.isFallenOff,
                    onCheckedChange = { onFallenOffChanged(!it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BoardColors.green,
                        uncheckedColor = BoardColors.green,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    border = ButtonDefaults.outlinedBorder.copy(brush = SolidColor(Color.Transparent)),
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.error),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Cancel, "")
                }
            }
        }
    }
}
