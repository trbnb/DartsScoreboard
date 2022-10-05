package de.trbnb.darts.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.domain.logic.MatchFactory
import de.trbnb.darts.domain.models.InOutRule
import de.trbnb.darts.domain.models.MatchOptions
import de.trbnb.darts.domain.models.Player
import de.trbnb.darts.domain.models.PlayerOrder
import de.trbnb.darts.domain.models.PlayerStartOrder
import de.trbnb.darts.domain.players.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val matchFactory: MatchFactory
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState(
            points = 301,
            legs = 0,
            sets = 1,
            outRule = InOutRule.STRAIGHT,
            inRule = InOutRule.STRAIGHT,
            players = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        playerRepository.getAll()
            .onEach { players ->
                _uiState.value = _uiState.value.copy(
                    players = players.map { player ->
                        PlayerItem(
                            player,
                            _uiState.value.players.firstOrNull { it.player == player }?.isSelected ?: false
                        )
                    }
                )
            }
            .launchIn(viewModelScope)
    }

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
        val selectedPlayers = _uiState.value.players
            .filter { it.isSelected }
            .map { it.player }

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
    }

    fun togglePlayerSelection(playerItem: PlayerItem, selected: Boolean) {
        _uiState.value = _uiState.value.copy(
            players = _uiState.value.players.map { oldItem ->
                oldItem.copy(
                    isSelected = when (playerItem.player) {
                        oldItem.player -> selected
                        else -> oldItem.isSelected
                    }
                )
            }
        )
    }

    fun deletePlayer(playerItem: PlayerItem) {
        viewModelScope.launch {
            playerRepository.delete(playerItem.player)
        }
    }

    data class UiState(
        val points: Int,
        val legs: Int,
        val sets: Int,
        val outRule: InOutRule,
        val inRule: InOutRule,
        val players: List<PlayerItem>
    ) {
        val canStartGame: Boolean
            get() = players.any { it.isSelected }
    }
    data class PlayerItem(val player: Player, val isSelected: Boolean)
}
