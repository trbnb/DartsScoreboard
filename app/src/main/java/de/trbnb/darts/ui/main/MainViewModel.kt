package de.trbnb.darts.ui.main

import androidx.databinding.Bindable
import androidx.hilt.lifecycle.ViewModelInject
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.models.MatchOptions
import de.trbnb.darts.models.PlayerOrder
import de.trbnb.darts.models.PlayerStartOrder
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.darts.ui.events.StartMatchEvent
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    playerRepository: PlayerRepository,
    playerViewModelFactory: PlayerViewModel.Factory,
    private val matchFactory: MatchFactory
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
        action = { createMatch() }
    )

    @get:Bindable
    var points by bindableInt(301).distinct()

    @get:Bindable
    var legs by bindableInt(1).distinct()

    @get:Bindable
    var sets by bindableInt(1).distinct()

    @get:Bindable
    var outRule by bindable(InOutRule.STRAIGHT).distinct()

    @get:Bindable
    var inRule by bindable(InOutRule.STRAIGHT).distinct()

    private fun createMatch() {
        val selectedPlayers = players
            ?.filter(PlayerViewModel::isSelected)
            ?.map(PlayerViewModel::player)
            ?: return

        val matchOptions = MatchOptions(
            points = points,
            sets = sets,
            legs = legs,
            inRule = inRule,
            outRule = outRule,
            playerStartOrder = PlayerStartOrder.SHUFFLE,
            playerOrder = PlayerOrder.WORST_STARTS
        )

        matchFactory.newMatch(selectedPlayers, matchOptions)
        eventChannel(StartMatchEvent)
    }
}

