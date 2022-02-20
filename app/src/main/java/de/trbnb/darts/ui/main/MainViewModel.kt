package de.trbnb.darts.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.models.Player
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : BaseViewModel() {
    private val playerSelectedListener: (PlayerViewModel) -> Unit = {
        startMatchCommand.onEnabledChanged()
    }

    val players = playerRepository.getAll()
        .map { players ->
            players.map {
                PlayerItem(it) {
                    viewModelScope.launch {
                        playerRepository.delete(it)
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val startMatchCommand = ruleCommand(
        enabledRule = { players.value.any { it.isSelected.value } },
        action = { configureMatch() }
    )

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.delete(player)
        }
    }

    private fun configureMatch() {
        val selectedPlayers = players.value
            .filter { it.isSelected.value }
            .map { it.player.id }

        eventChannel(MainEvent.ConfigureMatch(selectedPlayers))
    }
}

class PlayerItem(val player: Player, val remove: () -> Unit) {
    val isSelected = MutableStateFlow(false)

    fun selectPlayer(isSelected: Boolean) {
        this.isSelected.value = isSelected
    }
}
