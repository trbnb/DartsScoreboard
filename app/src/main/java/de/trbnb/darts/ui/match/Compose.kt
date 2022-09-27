@file:OptIn(ExperimentalLifecycleComposeApi::class)

package de.trbnb.darts.ui.match

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.models.*
import de.trbnb.darts.utils.forEach
import de.trbnb.darts.utils.px
import kotlinx.coroutines.delay

@Composable
fun MatchScreen(
    viewModel: MatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.matchState.collectAsStateWithLifecycle()
    when (val fixedUiState = uiState) {
        null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> MatchScreenLayout(
            fixedUiState,
            onThrow = viewModel::onThrow,
            onFallenOffChanged = viewModel::fallenOffChanged,
            onDeleteThrow = viewModel::deleteThrow,
            onConfirmTurn = viewModel::confirmTurn
        )
    }
}

@Composable
private fun MatchScreenLayout(
    uiState: MatchViewModel.UiState,
    onThrow: (Throw) -> Unit,
    onFallenOffChanged: (ThrowNumber, Boolean) -> Unit,
    onDeleteThrow: (ThrowNumber) -> Unit,
    onConfirmTurn: () -> Unit
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PlayerParticipationList(
            uiState.currentParticipationStats,
            uiState.currentPlayer,
            Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
        ThrowInfoRow(
            uiState = uiState,
            onFallenOffChanged = onFallenOffChanged,
            onDeleteThrow = onDeleteThrow,
            onConfirmTurn = onConfirmTurn,
            modifier = Modifier.fillMaxWidth().height(170.dp)
        )
        RemainingOverview(uiState)
        Input(
            onThrow = onThrow,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun PlayerParticipationList(
    currentParticipationStats: List<MatchLogic.CurrentParticipationStats>,
    currentPlayer: Player,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            PlayerParticipationHeader()
        }
        items(currentParticipationStats, key = { it.player.id }) { participationStats ->
            PlayerParticipationItemTemplate(
                participationStats,
                participationStats.player == currentPlayer
            )
        }
    }
}

@Composable
fun PlayerParticipationHeader() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(28.dp))
        Text(
            "Name",
            Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text("avg", Modifier.padding(end = 24.dp))
        Text("S", Modifier.width(20.dp), textAlign = TextAlign.End)
        Text("L", Modifier.width(20.dp), textAlign = TextAlign.End)
        Text("Punkte", Modifier.width(48.dp), textAlign = TextAlign.End)
    }
}

@Composable
fun PlayerParticipationItemTemplate(
    participationStats: MatchLogic.CurrentParticipationStats,
    isCurrentTurn: Boolean
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.DoubleArrow,
            null,
            Modifier
                .width(16.dp)
                .alpha(if (isCurrentTurn) 1f else 0f)
        )
        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .background(Color(participationStats.player.color))
                .width(4.dp)
                .height(24.dp)
        )
        Text(
            participationStats.player.name,
            Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text(String.format("%.2f", participationStats.matchParticipation.average), Modifier.padding(end = 24.dp))
        Text(participationStats.matchParticipation.wonSets.toString(), Modifier.width(20.dp), textAlign = TextAlign.End)
        Text(participationStats.currentSet.wonLegs.toString(), Modifier.width(20.dp), textAlign = TextAlign.End)
        Text(participationStats.remainingPoints.toString(), Modifier.width(48.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun ThrowInfoPreview() {
    Row(
        Modifier.fillMaxWidth().height(170.dp)
    ) {
        ThrowNumber.values().mapIndexed { index, number ->
            MatchViewModel.ThrowInfo(
                number,
                isNextThrow = index == 1,
                _throw = Throw(
                    Field.TWENTY,
                    Multiplier.DOUBLE,
                    if (index == 1) ThrowState.FALLEN_OFF else ThrowState.OK
                )
            )
        }.forEach { throwInfo ->
            ThrowInfoColumn(
                throwInfo,
                {},
                {},
                Modifier.weight(1f).fillMaxHeight()
            )
        }
        var enabledState by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(5000)
                enabledState = !enabledState
            }
        }

        TotalOverview(
            301,
            enabledState,
            {},
            Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun ThrowInfoRow(
    uiState: MatchViewModel.UiState,
    onFallenOffChanged: (ThrowNumber, Boolean) -> Unit,
    onDeleteThrow: (ThrowNumber) -> Unit,
    onConfirmTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
    ) {
        uiState.throwInfos.forEach { throwInfo ->
            ThrowInfoColumn(
                throwInfo = throwInfo,
                onFallenOffChanged = { onFallenOffChanged(throwInfo.number, it) },
                onDelete = { onDeleteThrow(throwInfo.number) },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        TotalOverview(
            uiState.currentTurn.value,
            confirmEnabled = uiState.isConfirmTurnAvailable,
            onConfirm = onConfirmTurn,
            modifier = Modifier.fillMaxHeight()
        )
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
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
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
            fontSize = 13.sp
        )
        Switch(!throwInfo.isFallenOff, { onFallenOffChanged(!it) })
        OutlinedButton(
            border = ButtonDefaults.outlinedBorder.copy(brush = SolidColor(MaterialTheme.colors.error)),
            onClick = onDelete,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.error),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Icon(
                Icons.Default.Cancel,
                ""
            )
        }
    }
}

@Composable
fun TotalOverview(
    points: Int,
    confirmEnabled: Boolean,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp).width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Total".uppercase(),
            style = MaterialTheme.typography.overline,
            fontSize = 13.sp
        )
        Text(
            points.toString(),
            style = MaterialTheme.typography.subtitle1,
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Box(Modifier.weight(1f).background(Color.Red))
        val animationOffset = 88.dp.px.toInt()
        AnimatedVisibility(
            confirmEnabled,
            enter = slideInHorizontally { animationOffset },
            exit = slideOutHorizontally { animationOffset }
        ) {
            FloatingActionButton(
                onClick = onConfirm,
                backgroundColor = MaterialTheme.colors.error
            ) {
                Icon(Icons.Default.Check, "confirm")
            }
        }
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun RemainingOverviewPreview() {
    RemainingOverview(
        301,
        List(10) {
            listOf(
                PotentialThrow(Field.EIGHT, Multiplier.TRIPLE),
                PotentialThrow(Field.BULL, Multiplier.DOUBLE),
                PotentialThrow(Field.TWENTY, Multiplier.DOUBLE)
            )
        }.takeIf { true }
    )
}

@Composable
fun RemainingOverview(uiState: MatchViewModel.UiState, modifier: Modifier = Modifier) {
    RemainingOverview(uiState.remainingPoints, uiState.suggestedFinishes, modifier)
}

@Composable
fun RemainingOverview(
    remainingPoints: Int,
    suggestedFinishes: List<List<PotentialThrow>>?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(64.dp)
    ) {
        Column(
            Modifier.width(96.dp).wrapContentHeight().padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Total".uppercase(),
                style = MaterialTheme.typography.overline,
                fontSize = 13.sp
            )
            Text(
                remainingPoints.toString(),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 24.sp
            )
        }
        Icon(Icons.Default.ChevronRight, "")
        Box(
            Modifier.weight(1f).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            when (suggestedFinishes) {
                null -> Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator(Modifier.size(32.dp))
                    Text(
                        "Lade Vorschläge...",
                        style = MaterialTheme.typography.subtitle1,
                        fontStyle = FontStyle.Italic
                    )
                }
                else -> when {
                    suggestedFinishes.isEmpty() -> {
                        Text(
                            "Kein Finish verfügbar",
                            style = MaterialTheme.typography.subtitle1,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> FinishSuggestionRow(
                        suggestedFinishes,
                        Modifier.fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
fun FinishSuggestionRow(
    finishSuggestions: List<List<PotentialThrow>>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(finishSuggestions) { finish ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(
                        Color(0xFF096B5E),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = finish.joinToString(separator = "\n") { it.description },
                    lineHeight = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}