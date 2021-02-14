package de.trbnb.darts.ui.match

import androidx.annotation.ColorRes
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.*
import de.trbnb.darts.databinding.ItemPossibleFinishBinding
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
import de.trbnb.mvvmbase.recyclerview.BindingListAdapter
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

    val showSets = matchLogic.match.matchOptions.sets > 1

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
    val info by matchLogic.remainingPoints
        .combine(matchLogic.turnState) { remainingPoints, state -> remainingPoints to state }
        .map { (remainingPoints, state) ->
            when {
                state == TurnState.Bust -> "BUST" to R.color.info_text_red
                remainingPoints == 0 -> "WON" to R.color.info_text_green
                else -> null
            }
        }
        .toBindable()

    @get:Bindable
    val remainingPoints by matchLogic.remainingPoints.map(Int::toString).toBindable()

    @get:Bindable
    val currentTotal by matchLogic.turn.map { it.value.toString() }.toBindable()

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

    @get:Bindable
    val finishSuggestions by matchLogic.suggestedFinishes
        .map { it.map(::PossibleFinishViewModel) }
        .toBindable()
        .beforeSet { old, new ->
            old?.destroyAll()
            new?.autoDestroy()
        }

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

class PossibleFinishesAdapter : BindingListAdapter<PossibleFinishViewModel, ItemPossibleFinishBinding>(
    R.layout.item_possible_finish,
    object : DiffUtil.ItemCallback<PossibleFinishViewModel>() {
        override fun areContentsTheSame(oldItem: PossibleFinishViewModel, newItem: PossibleFinishViewModel): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areItemsTheSame(oldItem: PossibleFinishViewModel, newItem: PossibleFinishViewModel): Boolean {
            return oldItem === newItem
        }
    }
)

@BindingAdapter("items")
fun RecyclerView.setItems(items: List<PossibleFinishViewModel>) {
    (adapter as? PossibleFinishesAdapter ?: PossibleFinishesAdapter().also { adapter = it })
        .submitList(items)
}

