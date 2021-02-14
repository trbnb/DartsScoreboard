package de.trbnb.darts.ui.main

import androidx.databinding.Bindable
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    playerRepository: PlayerRepository,
    playerViewModelFactory: PlayerViewModel.Factory
) : BaseViewModel(), CoroutineViewModel {
    private val playerSelectedListener: (PlayerViewModel) -> Unit = {
        startMatchCommand.onEnabledChanged()
    }

    @get:Bindable
    val players by playerRepository.getAll()
        .map { players -> players.map { playerViewModelFactory(it, playerSelectedListener) } }
        .toBindable()

    val startMatchCommand = ruleCommand(
        enabledRule = { players?.any(PlayerViewModel::isSelected) ?: false },
        action = { configureMatch() }
    )

    private fun configureMatch() {
        val selectedPlayers = players
            ?.filter(PlayerViewModel::isSelected)
            ?.map { it.player.id }
            ?: return

        eventChannel(MainEvent.ConfigureMatch(selectedPlayers))
    }
}
