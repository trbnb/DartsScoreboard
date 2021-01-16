package de.trbnb.darts.ui.match

import androidx.annotation.ColorRes
import androidx.databinding.Bindable
import androidx.hilt.lifecycle.ViewModelInject
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.*
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.*
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.darts.vibration.Vibrator
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.beforeSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import de.trbnb.mvvmbase.list.destroyAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MatchViewModel @Inject constructor(
    matchFactory: MatchFactory,
    private val vibrator: Vibrator,
    private val playerParticipationViewModelFactory: PlayerParticipationViewModel.Factory
) : BaseViewModel(), CoroutineViewModel {
    private val matchLogic = matchFactory.currentMatch ?: throw IllegalStateException()

    val playerViewModels by matchLogic.playerOrder
        .map { players -> players.map { playerParticipationViewModelFactory(matchLogic, it) } }
        .toBindable()
        .beforeSet { old, new ->
            old?.destroyAll()
            new?.bindEvents()?.autoDestroy()
        }

    val throwViewModels = ThrowNumber.values()
        .map { ThrowViewModel(it, matchLogic) }
        .bindEvents()
        .autoDestroy()

    @get:Bindable
    val info by matchLogic.turn
        .combine(matchLogic.turnState) { turn, state -> turn to state }
        .map { (turn, state) ->
            if (state == TurnState.Bust) {
                return@map "BUST" to R.color.info_text_red
            }

            val remaining = matchLogic.remainingPoints(matchLogic.currentPlayer.value)
            val scored = turn.value
            val newRemaining = remaining - scored
            if (newRemaining == 0) {
                return@map "WON" to R.color.info_text_green
            }
            return@map "$remaining - $scored = $newRemaining" to R.color.info_text_normal
        }
        .toBindable()

    @get:Bindable
    val turnState by matchLogic.turnState.toBindable()

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
    /*.chunked(7) { tripples -> listOf(tripples.map { it.first }, tripples.map { it.second }, tripples.map { it.third }) }
        .flatMap { it.flatten() }
        .map { (field, multiplier) -> FieldViewModel(field, multiplier, ::onFieldSelected, matchLogic, resourceProvider) }
        .bindEvents()
        .autoDestroy()*/

    @get:Bindable
    val finishSuggestion by matchLogic.suggestedFinish
        .map { it?.joinToString(separator = "      ", transform = PotentialThrow::description) }
        .toBindable()

    val confirmTurnCommand = ruleCommand(
        enabledRule = { turnState !is TurnState.Open },
        action = { matchLogic.confirmTurn() },
        dependentFields = listOf(::turnState)
    )

    @get:Bindable
    var isPlayerListScrollable by bindableBoolean(defaultValue = false)

    @get:ColorRes
    @get:Bindable("playerListScrollable")
    val playerListBackgroundRes: Int
        get() = when (isPlayerListScrollable) {
            true -> R.color.white
            false -> R.color.transparent
        }

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

    private fun onFieldSelected(field: Field, multiplier: Multiplier) {
        matchLogic.addThrow(field + multiplier)
    }
}

