package de.trbnb.darts.ui.main

import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.Menu
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.models.Player
import de.trbnb.mvvmbase.utils.observeAsState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.UUID

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel.eventChannel.addListener(this, ::onEvent)

        setContent {
            MainScreen()
        }

        supportActionBar?.subtitle = "Spielerauswahl"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return menuInflater.inflate(R.menu.activity_main, menu).let { true }
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_player -> NewPlayerDialogFragment().show(supportFragmentManager, null)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onEvent(event: Event) {
        when (event) {
            is MainEvent.ConfigureMatch -> MatchSetupDialogFragment(event.playerIds)
                .show(supportFragmentManager, null)
        }
    }*/
}

@Composable
@Preview(name = "Meh", showBackground = true, backgroundColor = WHITE.toLong())
fun Preview() {
    ScaffoldTest(
        players = listOf(
            Player(UUID.randomUUID(), "Thorben", 0),
            Player(UUID.randomUUID(), "Kristina", 0),
            Player(UUID.randomUUID(), "Andreas", 0),
            Player(UUID.randomUUID(), "Thalea", 0)
        ).map { PlayerItem(it) {} },
        onFabClicked = {}
    )
}

@Composable
fun ScaffoldTest(players: List<PlayerItem>, onFabClicked: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Darts Scoreboard") })
         },
        content = {
            PlayerList(modifier = Modifier.fillMaxSize() ,players = players)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClicked) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "")
            }
        }
    )
}

@Suppress("EXPERIMENTAL_API_USAGE")
@Composable
fun MainScreen() {
    val viewModel = viewModel<MainViewModel>()
    val playerState by viewModel::players.observeAsState()
    PlayerList(players = playerState.orEmpty())
}

@Composable
fun PlayerList(modifier: Modifier = Modifier, players: List<PlayerItem>) {
    LazyColumn(modifier) {
        items(players) { playerItem ->
            val isSelected = playerItem.isSelected.collectAsState()
            PlayerListRow(playerItem.player, isSelected, playerItem::selectPlayer) {}
        }
    }
}

@Composable
fun PlayerListRow(
    player: Player,
    selectedState: State<Boolean>,
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
        Checkbox(checked = selectedState.value, onCheckedChange = onSelected)
        Text(
            text = player.name,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.TwoTone.Delete, contentDescription = "", tint = Color.Red)
        }
    }
}