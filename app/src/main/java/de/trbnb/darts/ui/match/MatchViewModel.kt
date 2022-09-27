package de.trbnb.darts.ui.match

import androidx.annotation.ColorRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.R
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.*
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.darts.utils.CoroutinesViewModel
import de.trbnb.darts.utils.Triple
import de.trbnb.darts.utils.map
import de.trbnb.darts.vibration.Vibrator
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.DependsOn
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.utils.destroyAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    matchFactory: MatchFactory,
    private val vibrator: Vibrator,
    private val playerParticipationViewModelFactory: PlayerParticipationViewModel.Factory
) : BaseViewModel(), CoroutinesViewModel {
    private val matchLogic = matchFactory.currentMatch.value ?: throw IllegalStateException()

    val logic = matchFactory.currentMatch

    val matchState = matchFactory.currentMatch
        .flatMapConcat { matchLogic ->
            matchLogic?.state?.map {
                System.err.println(matchLogic?.state?.value?.currentPlayer); UiState(it) }
                ?: MutableStateFlow(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val subtitle = matchLogic.match.matchOptions.run {
        "$points · " + when (outRule) {
            InOutRule.STRAIGHT -> "straight out"
            InOutRule.DOUBLE -> "double out"
            InOutRule.TRIPLE -> "triple out"
            InOutRule.MASTER -> "master out"
        }
    }

    val showSets = matchLogic.match.matchOptions.sets > 1

    val playerViewModels by matchLogic.state
        .map { state -> state.playerOrder.map { playerParticipationViewModelFactory(matchLogic, it) } }
        .observe()
        .beforeSet { old, new ->
            old?.destroyAll()
            new?.bindEvents()?.autoDestroy()
        }

    val throwViewModels = ThrowNumber.values()
        .map { ThrowViewModel(it, matchLogic) }
        .bindEvents()
        .autoDestroy()

    val info by matchLogic.state
        .map { state ->
            when {
                state.turnState == TurnState.Bust -> "BUST" to R.color.info_text_red
                state.currentParticipationStats.firstOrNull { it.player == state.currentPlayer }?.remainingPoints == 0 -> "WON" to R.color.info_text_green
                else -> null
            }
        }
        .observe()

    val remainingPoints by matchLogic.state.map { state -> state.currentParticipationStats.firstOrNull { it.player == state.currentPlayer }?.remainingPoints.toString() }.observe()

    val currentTotal by matchLogic.state.map { it.currentTurn.value.toString() }.observe()

    val turnState by matchLogic.state.map { it.turnState }.observe()

    val currentPlayerIndex by matchLogic.state
        .map { it.playerOrder.indexOf(it.currentPlayer) }
        .observe(defaultValue = 0)

    val fieldViewModels = listOf(
        Triple(Field.MISS to Multiplier.SINGLE, Field.BULL to Multiplier.SINGLE, Field.BULL to Multiplier.DOUBLE),
        *(1..20).map { number ->
            Field.values().first { it.value == number }
                .let { field -> field.multipliers.map { multiplier -> field to multiplier } }
                .let { Triple(it[0], it[1], it[2]) }
        }.toTypedArray()
    ).let { tripples -> listOf(tripples.map { it.first }, tripples.map { it.second }, tripples.map { it.third }) }
        .flatten()
        .map { (field, multiplier) -> FieldViewModel(field, multiplier, ::onFieldSelected, matchLogic) }
        .bindEvents()
        .autoDestroy()

    val finishSuggestions by matchLogic.state
        .map { it.suggestedFinishes?.map(::PossibleFinishViewModel).orEmpty() }
        .observe()
        .beforeSet { old, new ->
            old?.destroyAll()
            new?.autoDestroy()
        }

    val confirmTurnCommand = ruleCommand(
        enabledRule = { turnState !is TurnState.Open },
        action = { matchLogic.confirmTurn() },
        dependencyProperties = listOf(::turnState)
    )

    fun confirmTurn() {
        logic.value?.confirmTurn()
    }

    fun fallenOffChanged(throwNumber: ThrowNumber, fallenOff: Boolean) {
        logic.value?.fellOff(throwNumber, fallenOff)
    }

    fun deleteThrow(throwNumber: ThrowNumber) {
        logic.value?.removeThrow(throwNumber)
    }

    var isPlayerListScrollable by bindableBoolean(defaultValue = false)

    @get:ColorRes
    @DependsOn("playerListScrollable")
    val playerListBackgroundRes: Int
        get() = when (isPlayerListScrollable) {
            true -> R.color.white
            false -> R.color.transparent
        }

    val canUndoTurn by matchLogic.state
        .map { matchLogic.canUndoTurnConfirmation() }
        .observe(defaultValue = false)

    init {
        matchLogic.state.map { it.turnState }.distinctUntilChanged()
            .filterIsInstance<TurnState.Bust>()
            .onEach { vibrator.vibrateShortly() }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            matchLogic.gameEnded.await()

            eventChannel(CloseEvent)
        }
    }

    fun undoTurn() = matchLogic.undoTurnConfirmation()

    fun onThrow(_throw: Throw) {
        logic.value?.addThrow(_throw)
    }

    private fun onFieldSelected(field: Field, multiplier: Multiplier) {
        matchLogic.addThrow(field + multiplier)
    }

    class UiState(
        matchState: MatchLogic.State,
    ) : MatchLogic.State by matchState {
        val throwInfos: Triple<ThrowInfo> = ThrowNumber.tripple.map { number ->
            ThrowInfo(
                number,
                matchState.currentTurn.nextThrow == number,
                matchState.currentTurn[number]
            )
        }

        val isConfirmTurnAvailable = matchState.currentTurn.nextThrow == null
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

