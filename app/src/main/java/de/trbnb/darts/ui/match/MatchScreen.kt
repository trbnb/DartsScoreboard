@file:OptIn(ExperimentalLifecycleComposeApi::class)

package de.trbnb.darts.ui.match

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.Throw
import de.trbnb.darts.models.ThrowNumber
import de.trbnb.darts.ui.navigation.LocalNavController

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
            onConfirmTurn = viewModel::confirmTurn,
            onUndo = viewModel::undoTurn
        )
    }

    BackHandler()
}

@Composable
private fun BackHandler() {
    val navController = LocalNavController.current
    var dialogIsShown by remember { mutableStateOf(false) }
    androidx.activity.compose.BackHandler(true) {
        dialogIsShown = !dialogIsShown
    }

    if (dialogIsShown) {
        AlertDialog(
            onDismissRequest = { dialogIsShown = false },
            title = { Text("Match schließen?") },
            buttons = {
                Button(onClick = navController::popBackStack) {
                    Text("Schließen".uppercase())
                }
            }
        )
    }
}

@Composable
private fun MatchScreenLayout(
    uiState: MatchViewModel.UiState,
    onThrow: (Throw) -> Unit,
    onFallenOffChanged: (ThrowNumber, Boolean) -> Unit,
    onDeleteThrow: (ThrowNumber) -> Unit,
    onConfirmTurn: () -> Unit,
    onUndo: () -> Unit
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max = 140.dp).padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            PlayerParticipationList(
                showSets = uiState.showSets,
                currentParticipationStats = uiState.currentParticipationStats,
                currentPlayer = uiState.currentPlayer,
                modifier = Modifier.weight(1f).wrapContentHeight()
            )
            Box(
                Modifier.size(64.dp).align(Alignment.CenterVertically)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    uiState.canUndoTurnConfirmation,
                    enter = slideInHorizontally { it },
                    exit = slideOutHorizontally { it }
                ) {
                    OutlinedButton(
                        onClick = onUndo,
                        enabled = uiState.canUndoTurnConfirmation,
                        modifier = Modifier.fillMaxWidth().size(64.dp),
                        border = ButtonDefaults.outlinedBorder.copy(brush = SolidColor(Color.Transparent)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Undo, "Undo")
                    }
                }
            }

        }
        ThrowInfoRow(
            uiState = uiState,
            onFallenOffChanged = onFallenOffChanged,
            onDeleteThrow = onDeleteThrow,
            onConfirmTurn = onConfirmTurn,
            modifier = Modifier.fillMaxWidth().height(136.dp)
        )
        RemainingOverview(uiState)
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                uiState.turnState is TurnState.Open,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.matchParentSize()
            ) {
                Input(onThrow = onThrow, modifier = Modifier.matchParentSize())
            }

            androidx.compose.animation.AnimatedVisibility(
                uiState.turnState !is TurnState.Open,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it }
            ) {
                Column(
                    modifier = Modifier.matchParentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(48.dp, alignment = Alignment.CenterVertically)
                ) {
                    Text(
                        uiState.pointsText,
                        style = MaterialTheme.typography.h4
                    )

                    Button(
                        onClick = onConfirmTurn,
                        modifier = Modifier.height(128.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp)
                    ) {
                        Icon(Icons.Default.Check, "confirm", Modifier.size(48.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Bestätigen".uppercase(), fontSize = 24.sp)
                    }
                }
            }
        }
    }
}