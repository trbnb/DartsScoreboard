package de.trbnb.darts.ui.main

import android.graphics.Color.WHITE
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.models.Player
import de.trbnb.darts.ui.main.newplayer.NewPlayerSheet
import de.trbnb.darts.ui.matchsetup.MatchSetupDialog
import de.trbnb.darts.ui.navigation.LocalNavController
import de.trbnb.darts.ui.navigation.NavHost
import de.trbnb.darts.ui.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NavHost()
        }
    }
}

@Composable
@Preview(name = "Meh", showBackground = true, backgroundColor = WHITE.toLong())
fun Preview() {
    PlayerList(
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
    )
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

    var isMatchSetupVisible by remember { mutableStateOf(false) }
    val matchSetupSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        confirmStateChange = { modalBottomSheetValue ->
            isMatchSetupVisible = when (modalBottomSheetValue) {
                ModalBottomSheetValue.Hidden -> false
                ModalBottomSheetValue.Expanded, ModalBottomSheetValue.HalfExpanded -> true
            }
            true
        }
    )

    LaunchedEffect(isMatchSetupVisible) {
        if (isMatchSetupVisible) {
            matchSetupSheetState.animateTo(ModalBottomSheetValue.Expanded)
        } else {
            matchSetupSheetState.hide()
        }
    }

    BackHandler(enabled = isNewPlayerSheetVisible) { isNewPlayerSheetVisible = false }

    PlayerList(
        players = uiState.players,
        canStartGame = uiState.canStartGame,
        onPlayerSelectionChanged = viewModel::togglePlayerSelection,
        onNewPlayerClick = { isNewPlayerSheetVisible = true },
        onPlayerRemove = viewModel::deletePlayer,
        onMatchSetupClick = { isMatchSetupVisible = true }
    )

    NewPlayerSheet(
        sheetState = newPlayerSheetState,
        onDismiss = { isNewPlayerSheetVisible = false }
    )

    val navController = LocalNavController.current

    MatchSetupDialog(
        matchSetupSheetState,
        onStartMatchClick = {
            isMatchSetupVisible = false
            navController.navigate(Screen.Match.route)
        }
    )
}

@Composable
fun PlayerList(
    players: List<MainViewModel.PlayerItem>,
    canStartGame: Boolean,
    onPlayerSelectionChanged: (MainViewModel.PlayerItem, Boolean) -> Unit,
    onNewPlayerClick: () -> Unit,
    onPlayerRemove: (MainViewModel.PlayerItem) -> Unit,
    onMatchSetupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Darts Scoreboard") },
                actions = {
                    Icon(
                        painterResource(R.drawable.ic_baseline_person_add_24),
                        "",
                        Modifier.clickable(onClick = onNewPlayerClick)
                    )
                }
            )
        },
        content = { padding ->
            LazyColumn(Modifier.padding(padding)) {
                items(players) { playerItem ->
                    PlayerListRow(
                        playerItem = playerItem,
                        onSelected = { onPlayerSelectionChanged(playerItem, it) },
                        onRemove = { onPlayerRemove(playerItem) }
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                canStartGame,
                enter = slideInVertically { 2*it },
                exit = slideOutVertically { 2*it }
            ) {
                FloatingActionButton(
                    onClick = onMatchSetupClick
                ) {
                    Icon(painterResource(R.drawable.ic_darts_board_24dp), "")
                }
            }
        }
    )
}

@Composable
fun PlayerListRow(
    playerItem: MainViewModel.PlayerItem,
    onSelected: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .padding(8.dp)
                .size(16.dp)) {
            drawCircle(color = Color.Red)
            drawCircle(color = Color.Gray, style = Stroke(width = 1.dp.toPx()))
        }
        Checkbox(checked = playerItem.isSelected, onCheckedChange = onSelected)
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