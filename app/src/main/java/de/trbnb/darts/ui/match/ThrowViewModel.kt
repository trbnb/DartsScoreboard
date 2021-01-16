package de.trbnb.darts.ui.match

import androidx.databinding.Bindable
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.ThrowNumber
import de.trbnb.darts.models.ThrowState
import de.trbnb.darts.models.description
import de.trbnb.darts.models.get
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class ThrowViewModel(
    private val throwNumber: ThrowNumber,
    private val logic: MatchLogic
) : BaseViewModel(), CoroutineViewModel {
    val title = "Wurf " + when (throwNumber) {
        ThrowNumber.ONE -> "1"
        ThrowNumber.TWO -> "2"
        ThrowNumber.THREE -> "3"
    }

    @get:Bindable
    val thisThrow by logic.turn.map { it[throwNumber] }.toBindable()

    @get:Bindable("thisThrow")
    val points: String
        get() = when (val throww = thisThrow) {
            null -> "-"
            else -> throww.let { it.field.value * it.multiplier.value }.toString()
        }

    @get:Bindable("thisThrow")
    var isFallenOff: Boolean
        get() = thisThrow?.state == ThrowState.FALLEN_OFF
        set(value) { logic.fellOff(throwNumber, value) }

    @get:Bindable("thisThrow")
    val description: String
        get() = thisThrow?.description ?: ""

    @get:Bindable
    val isNextThrow by logic.turnState
        .map { it is TurnState.Open && it.nextThrow == throwNumber }
        .toBindable(defaultValue = false)

    val removeCommand = ruleCommand(
        enabledRule = { thisThrow != null },
        action = { logic.remove(throwNumber) },
        dependentFields = listOf(::thisThrow)
    )
}