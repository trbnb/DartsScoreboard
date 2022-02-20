package de.trbnb.darts.ui.match

import androidx.annotation.ColorRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.R
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.Field
import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.models.Multiplier
import de.trbnb.darts.models.ThrowNumber
import de.trbnb.darts.models.plus
import de.trbnb.darts.models.value
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.darts.utils.CoroutinesViewModel
import de.trbnb.darts.vibration.Vibrator
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.DependsOn
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.utils.destroyAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MatchViewModel @Inject constructor(
    matchFactory: MatchFactory,
    private val vibrator: Vibrator,
    private val playerParticipationViewModelFactory: PlayerParticipationViewModel.Factory
) : BaseViewModel(), CoroutinesViewModel {
    private val matchLogic = matchFactory.currentMatch ?: throw IllegalStateException()

    val subtitle = matchLogic.match.matchOptions.run {
        "$points · " + when (outRule) {
            InOutRule.STRAIGHT -> "straight out"
            InOutRule.DOUBLE -> "double out"
            InOutRule.TRIPLE -> "triple out"
            InOutRule.MASTER -> "master out"
        }
    }

    val showSets = matchLogic.match.matchOptions.sets > 1

    val playerViewModels by matchLogic.playerOrder
        .map { players -> players.map { playerParticipationViewModelFactory(matchLogic, it) } }
        .observe()
        .beforeSet { old, new ->
            old?.destroyAll()
            new?.bindEvents()?.autoDestroy()
        }

    val throwViewModels = ThrowNumber.values()
        .map { ThrowViewModel(it, matchLogic) }
        .bindEvents()
        .autoDestroy()

    val info by matchLogic.remainingPoints
        .combine(matchLogic.turnState) { remainingPoints, state -> remainingPoints to state }
        .map { (remainingPoints, state) ->
            when {
                state == TurnState.Bust -> "BUST" to R.color.info_text_red
                remainingPoints == 0 -> "WON" to R.color.info_text_green
                else -> null
            }
        }
        .observe()

    val remainingPoints by matchLogic.remainingPoints.map(Int::toString).observe()

    val currentTotal by matchLogic.turn.map { it.value.toString() }.observe()

    val turnState by matchLogic.turnState.observe()

    val currentPlayerIndex by matchLogic.currentPlayer.combine(matchLogic.playerOrder) { player, order -> player to order }
        .map { (player, order) -> order.indexOf(player) }
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

    val finishSuggestions by matchLogic.suggestedFinishes
        .map { it.map(::PossibleFinishViewModel) }
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

    var isPlayerListScrollable by bindableBoolean(defaultValue = false)

    @get:ColorRes
    @DependsOn("playerListScrollable")
    val playerListBackgroundRes: Int
        get() = when (isPlayerListScrollable) {
            true -> R.color.white
            false -> R.color.transparent
        }

    val canUndoTurn by matchLogic.turn.map { matchLogic.canUndoTurnConfirmation() }.observe(defaultValue = false)

    init {
        matchLogic.turnState.distinctUntilChanged()
            .filterIsInstance<TurnState.Bust>()
            .onEach { vibrator.vibrateShortly() }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            matchLogic.gameEnded.await()

            eventChannel(CloseEvent)
        }
    }

    fun undoTurn() = matchLogic.undoTurnConfirmation()

    private fun onFieldSelected(field: Field, multiplier: Multiplier) {
        matchLogic.addThrow(field + multiplier)
    }
}

