package de.trbnb.darts.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.R
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.*
import de.trbnb.darts.utils.Triple
import de.trbnb.darts.utils.map
import de.trbnb.darts.vibration.Vibrator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    matchFactory: MatchFactory,
    private val vibrator: Vibrator
) : ViewModel() {
    val logic = matchFactory.currentMatch

    val matchState = matchFactory.currentMatch
        .flatMapConcat { matchLogic ->
            matchLogic?.state?.map { UiState(it) }
                ?: MutableStateFlow(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val info = logic.value?.state
        ?.map { state ->
            when {
                state.turnState == TurnState.Bust -> "BUST" to R.color.info_text_red
                state.currentParticipationStats.firstOrNull { it.player == state.currentPlayer }?.remainingPoints == 0 -> "WON" to R.color.info_text_green
                else -> null
            }
        }

    fun confirmTurn() {
        logic.value?.confirmTurn()
    }

    fun fallenOffChanged(throwNumber: ThrowNumber, fallenOff: Boolean) {
        logic.value?.fellOff(throwNumber, fallenOff)
    }

    fun deleteThrow(throwNumber: ThrowNumber) {
        logic.value?.removeThrow(throwNumber)
    }

    init {
        matchState.map { it?.turnState }
            .distinctUntilChanged()
            .filterIsInstance<TurnState.Bust>()
            .onEach { this.vibrator.vibrateShortly() }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            logic.value?.gameEnded?.await()

            //eventChannel(CloseEvent)
        }
    }

    fun undoTurn() = logic.value?.undoTurnConfirmation()

    fun onThrow(_throw: Throw) {
        logic.value?.addThrow(_throw)
    }

    class UiState(
        matchState: MatchLogic.State,
    ) : MatchLogic.State by matchState {
        val throwInfos: Triple<ThrowInfo> = ThrowNumber.tripple.map { number ->
            ThrowInfo(
                number,
                matchState.turnState.run { this is TurnState.Open && nextThrow == number },
                matchState.currentTurn[number]
            )
        }

        val isConfirmTurnAvailable = matchState.turnState !is TurnState.Open
        val showSets = matchState.match.matchOptions.sets > 1

        val subtitle = matchState.match.matchOptions.run {
            "$points · " + when (outRule) {
                InOutRule.STRAIGHT -> "straight out"
                InOutRule.DOUBLE -> "double out"
                InOutRule.TRIPLE -> "triple out"
                InOutRule.MASTER -> "master out"
            }
        }
    }

    class ThrowInfo(
        val number: ThrowNumber,
        val isNextThrow: Boolean,
        private val _throw: Throw?
    ) {
        val value: String get() = _throw?.rawValue?.toString() ?: "-"
        val description: String get() = _throw?.description ?: "-"
        val isFallenOff: Boolean get() = _throw?.state == ThrowState.FALLEN_OFF
    }
}

