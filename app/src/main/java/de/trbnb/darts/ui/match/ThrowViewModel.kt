package de.trbnb.darts.ui.match

import androidx.lifecycle.viewModelScope
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.ThrowNumber
import de.trbnb.darts.models.ThrowState
import de.trbnb.darts.models.description
import de.trbnb.darts.models.get
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.DependsOn
import de.trbnb.mvvmbase.commands.ruleCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@ExperimentalCoroutinesApi
class ThrowViewModel(
    private val throwNumber: ThrowNumber,
    private val logic: MatchLogic
) : BaseViewModel() {
    val title = "Wurf " + when (throwNumber) {
        ThrowNumber.ONE -> "1"
        ThrowNumber.TWO -> "2"
        ThrowNumber.THREE -> "3"
    }

    val thisThrow = logic.turn.map { it[throwNumber] }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @DependsOn("thisThrow")
    val points: String
        get() = when (val throww = thisThrow.value) {
            null -> "-"
            else -> throww.let { it.field.value * it.multiplier.value }.toString()
        }

    @DependsOn("thisThrow")
    var isFallenOff: Boolean
        get() = thisThrow.value?.state == ThrowState.FALLEN_OFF
        set(value) { logic.fellOff(throwNumber, value) }

    @DependsOn("thisThrow")
    val description: String
        get() = thisThrow.value?.description ?: ""

    val isNextThrow = logic.turnState
        .map { it is TurnState.Open && it.nextThrow == throwNumber }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val removeCommand = ruleCommand(
        enabledRule = { thisThrow != null },
        action = { logic.removeThrow(throwNumber) },
        dependencyProperties = listOf(::thisThrow)
    )
}