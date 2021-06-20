package de.trbnb.darts.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.models.Player
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : BaseViewModel(), CoroutineViewModel {
    private val playerSelectedListener: (PlayerViewModel) -> Unit = {
        startMatchCommand.onEnabledChanged()
    }

    val players by playerRepository.getAll()
        .map { players ->
            players.map {
                PlayerItem(it) {
                    viewModelScope.launch {
                        playerRepository.delete(it)
                    }
                }
            }
        }.toBindable(defaultValue = emptyList())

    val startMatchCommand = ruleCommand(
        enabledRule = { players.any { it.isSelected.value } },
        action = { configureMatch() }
    )

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerRepository.delete(player)
        }
    }

    private fun configureMatch() {
        val selectedPlayers = players
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
