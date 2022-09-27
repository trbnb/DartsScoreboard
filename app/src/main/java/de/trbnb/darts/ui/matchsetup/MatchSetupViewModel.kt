package de.trbnb.darts.ui.matchsetup

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
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchSetupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchFactory: MatchFactory,
    private val playerRepository: PlayerRepository
) : BaseStateSavingViewModel(savedStateHandle) {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState(
            points = 301,
            legs = 0,
            sets = 0,
            outRule = InOutRule.STRAIGHT,
            inRule = InOutRule.STRAIGHT
        )
    )
    val uiState = _uiState.asStateFlow()

    fun setPoints(points: Int) {
        _uiState.value = _uiState.value.copy(points = points)
    }

    fun setSets(sets: Int) {
        _uiState.value = _uiState.value.copy(sets = sets)
    }

    fun setLegs(legs: Int) {
        _uiState.value = _uiState.value.copy(legs = legs)
    }

    fun setOutRule(outRule: InOutRule) {
        _uiState.value = _uiState.value.copy(outRule = outRule)
    }

    fun setInRule(inRule: InOutRule) {
        _uiState.value = _uiState.value.copy(inRule = inRule)
    }

    fun createMatch() = viewModelScope.launch {
        val selectedPlayers = playerRepository.getByIds(emptyList())

        val matchOptions = MatchOptions(
            points = uiState.value.points,
            sets = uiState.value.sets,
            legs = uiState.value.legs,
            inRule = uiState.value.inRule,
            outRule = uiState.value.outRule,
            playerStartOrder = PlayerStartOrder.SHUFFLE,
            playerOrder = PlayerOrder.WORST_STARTS
        )

        matchFactory.newMatch(selectedPlayers, matchOptions)
        eventChannel(StartMatchEvent)
    }

    data class UiState(
        val points: Int,
        val legs: Int,
        val sets: Int,
        val outRule: InOutRule,
        val inRule: InOutRule
    )
}

