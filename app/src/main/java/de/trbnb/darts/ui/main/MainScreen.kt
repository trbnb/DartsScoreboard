package de.trbnb.darts.ui.main

import android.graphics.Color.WHITE
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.trbnb.darts.domain.models.InOutRule
import de.trbnb.darts.ui.main.newplayer.NewPlayerSheet
import de.trbnb.darts.ui.match.BoardColors
import de.trbnb.darts.ui.matchsetup.MatchSetupLayout
import de.trbnb.darts.ui.navigation.LocalNavController
import de.trbnb.darts.ui.navigation.Screen

@Composable
@Preview(name = "Meh", showBackground = true, backgroundColor = WHITE.toLong())
fun Preview() {
    /*PlayerList(
        players = listOf(
            Player(UUID.randomUUID(), "Thorben", 0),
            Player(UUID.randomUUID(), "Kristina", 0),
            Player(UUID.randomUUID(), "Andreas", 0),
            Player(UUID.randomUUID(), "Thalea", 0)
        ).map { MainViewModel.PlayerItem(it, false) },
        false,
        onPlayerSelectionChanged = { _, _ -> },
        onNewPlayerClick = {},
        onPlayerRemove = {},
        onMatchSetupClick = {}
    )*/
}

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isNewPlayerSheetVisible by remember { mutableStateOf(false) }
    val newPlayerSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        confirmStateChange = { modalBottomSheetValue ->
            isNewPlayerSheetVisible = when (modalBottomSheetValue) {
                ModalBottomSheetValue.Hidden -> false
                ModalBottomSheetValue.Expanded, ModalBottomSheetValue.HalfExpanded -> true
            }
            true
        }
    )

    LaunchedEffect(isNewPlayerSheetVisible) {
        if (isNewPlayerSheetVisible) {
            newPlayerSheetState.animateTo(ModalBottomSheetValue.Expanded)
        } else {
            newPlayerSheetState.hide()
        }
    }

    BackHandler(enabled = isNewPlayerSheetVisible) { isNewPlayerSheetVisible = false }

    val navController = LocalNavController.current

    MainScreenLayout(
        uiState = uiState,
        onPlayerSelectionChanged = viewModel::togglePlayerSelection,
        onNewPlayerClick = { isNewPlayerSheetVisible = true },
        onPlayerRemove = viewModel::deletePlayer,
        onPointsChanged = viewModel::setPoints,
        onSetsChanged = viewModel::setSets,
        onLegsChanged = viewModel::setLegs,
        onOutRuleChanged = viewModel::setOutRule,
        onInRuleChanged = viewModel::setInRule,
        onStartMatchClick = {
            viewModel.createMatch()
            navController.navigate(Screen.Match.route)
        }
    )

    NewPlayerSheet(
        sheetState = newPlayerSheetState,
        onDismiss = { isNewPlayerSheetVisible = false }
    )
}

@Composable
fun MainScreenLayout(
    uiState: MainViewModel.UiState,
    onPlayerSelectionChanged: (MainViewModel.PlayerItem, Boolean) -> Unit,
    onNewPlayerClick: () -> Unit,
    onPlayerRemove: (MainViewModel.PlayerItem) -> Unit,
    onPointsChanged: (points: Int) -> Unit,
    onSetsChanged: (sets: Int) -> Unit,
    onLegsChanged: (legs: Int) -> Unit,
    onOutRuleChanged: (outRule: InOutRule) -> Unit,
    onInRuleChanged: (inRule: InOutRule) -> Unit,
    onStartMatchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                title = { Text("Darts Scoreboard", color = MaterialTheme.colors.primary) }
            )
        },
        content = { padding ->
            Column(Modifier.padding(padding)) {
                Box(Modifier.weight(1f)) {
                    PlayerList(
                        playerItems = uiState.players,
                        onPlayerSelected = onPlayerSelectionChanged,
                        onPlayerRemove = onPlayerRemove
                    )
                    FloatingActionButton(
                        onClick = onNewPlayerClick,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Default.PersonAddAlt1, "", tint = Color.White)
                    }
                }
                Card(elevation = 8.dp) {
                    MatchSetupLayout(
                        canStartGame = uiState.canStartGame,
                        points = uiState.points,
                        sets = uiState.sets,
                        legs = uiState.legs,
                        outRule = uiState.outRule,
                        inRule = uiState.inRule,
                        onPointsChanged = onPointsChanged,
                        onSetsChanged = onSetsChanged,
                        onLegsChanged = onLegsChanged,
                        onOutRuleChanged = onOutRuleChanged,
                        onInRuleChanged = onInRuleChanged,
                        onStartMatchClick = onStartMatchClick,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun PlayerList(
    playerItems: List<MainViewModel.PlayerItem>,
    onPlayerSelected: (MainViewModel.PlayerItem, Boolean) -> Unit,
    onPlayerRemove: (MainViewModel.PlayerItem) -> Unit
) {
    LazyColumn {
        items(playerItems) { playerItem ->
            PlayerListRow(
                playerItem = playerItem,
                onSelected = { onPlayerSelected(playerItem, it) },
                onRemove = { onPlayerRemove(playerItem) }
            )
        }
    }
}

@Composable
fun PlayerListRow(
    playerItem: MainViewModel.PlayerItem,
    onSelected: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = { onSelected(!playerItem.isSelected) })
    ) {
        Canvas(
            Modifier
                .padding(8.dp)
                .size(16.dp)) {
            drawCircle(color = Color(playerItem.player.color.toULong()))
            drawCircle(color = Color.Gray, style = Stroke(width = 1.dp.toPx()))
        }
        Checkbox(
            checked = playerItem.isSelected,
            onCheckedChange = onSelected,
            colors = CheckboxDefaults.colors(
                checkedColor = BoardColors.green,
                uncheckedColor = BoardColors.green,
                checkmarkColor = Color.White
            )
        )
        Text(
            text = playerItem.player.name,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.TwoTone.Delete, contentDescription = "", tint = Color.Red)
        }
    }
}