package de.trbnb.darts.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.domain.logic.MatchFactory
import de.trbnb.darts.domain.logic.MatchLogic
import de.trbnb.darts.domain.logic.TurnState
import de.trbnb.darts.domain.models.Field
import de.trbnb.darts.domain.models.InOutRule
import de.trbnb.darts.domain.models.Multiplier
import de.trbnb.darts.domain.models.Throw
import de.trbnb.darts.domain.models.ThrowNumber
import de.trbnb.darts.domain.models.ThrowState
import de.trbnb.darts.domain.models.description
import de.trbnb.darts.domain.models.get
import de.trbnb.darts.domain.models.plus
import de.trbnb.darts.domain.models.rawValue
import de.trbnb.darts.domain.models.value
import de.trbnb.darts.domain.utils.Triple
import de.trbnb.darts.domain.utils.map
import de.trbnb.darts.domain.vibration.Vibrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    matchFactory: MatchFactory,
    private val vibrator: Vibrator
) : ViewModel() {
    val logic = matchFactory.currentMatch

    @OptIn(FlowPreview::class)
    val matchState = matchFactory.currentMatch
        .flatMapConcat { matchLogic ->
            matchLogic?.state?.map { UiState(it) }
                ?: MutableStateFlow(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun confirmTurn() {
        viewModelScope.launch(Dispatchers.IO) {
            logic.value?.confirmTurn()
        }
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

        val pointsText = when {
            matchState.currentTurn.run {
                listOf(first, second, third).run {
                    any { it == Field.TWENTY + Multiplier.SINGLE }
                            && any { it == Field.FIVE + Multiplier.SINGLE }
                            && any { it == Field.ONE + Multiplier.SINGLE }
                }
            } -> "DAS ÜBLICHE! \uD83E\uDD73"
            else -> "${matchState.currentTurn.value} Punkte"
        }
    }

    class ThrowInfo(
        val number: ThrowNumber,
        val isNextThrow: Boolean,
        val _throw: Throw?
    ) {
        val value: String get() = _throw?.rawValue?.toString() ?: "-"
        val description: String get() = _throw?.description ?: "-"
        val isFallenOff: Boolean get() = _throw?.state == ThrowState.FALLEN_OFF
    }
}

