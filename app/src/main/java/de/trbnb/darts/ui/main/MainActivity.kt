package de.trbnb.darts.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.databinding.Observable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.BR
import de.trbnb.darts.R
import de.trbnb.darts.models.Player
import de.trbnb.darts.ui.main.newplayer.NewPlayerDialogFragment
import de.trbnb.darts.ui.matchsetup.MatchSetupDialogFragment
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.addListener
import de.trbnb.mvvmbase.utils.addOnPropertyChangedCallback
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.UUID
import kotlin.reflect.KProperty0

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.eventChannel.addListener(this, ::onEvent)

        setContent {
            MainScreen()
        }

        supportActionBar?.subtitle = "Spielerauswahl"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return menuInflater.inflate(R.menu.activity_main, menu).let { true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    PlayerList(players = listOf(
        Player(UUID.randomUUID(), "Thorben", 0),
        Player(UUID.randomUUID(), "Kristina", 0),
        Player(UUID.randomUUID(), "Andreas", 0),
        Player(UUID.randomUUID(), "Thalea", 0)
    ))
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainScreen() {
    val viewModel = viewModel<MainViewModel>()
    val playerState = viewModel::players.observeAsState(observable = viewModel)
    PlayerList(players = playerState.value.map { it.player })
}

@Composable
fun PlayerList(players: List<Player>) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(players) { player ->
            Row {
                Image(ColorPainter(Color(player.color)), "ff", modifier = Modifier.clip(CircleShape))
                Text(
                    text = player.name,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun <T> KProperty0<T>.observeAsState(observable: Observable): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(get()) }
    DisposableEffect(this, lifecycleOwner) {
        val disposable = observeBindable(observable, lifecycleOwner, invokeImmediately = false, action = { value -> state.value = value })
        onDispose(disposable::dispose)
    }
    return state
}

internal inline fun <T> KProperty0<T>.observeBindable(
    observable: Observable,
    lifecycleOwner: LifecycleOwner,
    invokeImmediately: Boolean = true,
    crossinline action: (T) -> Unit
): Disposable {
    val propertyId = resolveFieldId().takeUnless { it == BR._all } ?: throw IllegalArgumentException("Property isn't bindable")

    val propertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, changedPropertyId: Int) {
            if (changedPropertyId == propertyId) {
                action(get())
            }
        }
    }

    observable.addOnPropertyChangedCallback(lifecycleOwner, propertyChangedCallback)

    if (invokeImmediately) {
        action(get())
    }

    return CompletableDisposable { observable.removeOnPropertyChangedCallback(propertyChangedCallback) }
}

interface Disposable {
    fun dispose()
}

class CompletableDisposable(private val onDispose: () -> Unit) : Disposable {
    override fun dispose() {
        onDispose()
    }
}