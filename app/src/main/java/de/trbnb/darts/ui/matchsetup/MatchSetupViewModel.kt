package de.trbnb.darts.ui.matchsetup

import androidx.databinding.Bindable
import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.models.MatchOptions
import de.trbnb.darts.models.PlayerOrder
import de.trbnb.darts.models.PlayerStartOrder
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.darts.ui.events.StartMatchEvent
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MatchSetupViewModel @Inject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val matchFactory: MatchFactory,
    private val playerRepository: PlayerRepository
) : BaseStateSavingViewModel(savedStateHandle) {
    var playerIds by bindable<List<UUID>>(listOf())

    val startMatchCommand = simpleCommand {
        createMatch()
    }

    @get:Bindable
    var points by bindableInt(301).distinct()

    @get:Bindable
    var legs by bindableInt(0).distinct()

    @get:Bindable("legs")
    val legsText: String
        get() = if (legs == 0) "∞" else legs.toString()

    @get:Bindable
    var sets by bindableInt(1).distinct()

    @get:Bindable
    var outRule by bindable(InOutRule.STRAIGHT).distinct()

    @get:Bindable
    var inRule by bindable(InOutRule.STRAIGHT).distinct()

    private fun createMatch() = viewModelScope.launch {
        val selectedPlayers = playerRepository.getByIds(playerIds)

        val matchOptions = MatchOptions(
            points = points,
            sets = sets,
            legs = legs.takeUnless { it == 0 } ?: Int.MAX_VALUE,
            inRule = inRule,
            outRule = outRule,
            playerStartOrder = PlayerStartOrder.SHUFFLE,
            playerOrder = PlayerOrder.WORST_STARTS
        )

        matchFactory.newMatch(selectedPlayers, matchOptions)
        eventChannel(StartMatchEvent)
    }
}

