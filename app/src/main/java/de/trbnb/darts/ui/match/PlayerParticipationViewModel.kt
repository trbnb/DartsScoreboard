package de.trbnb.darts.ui.match

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import de.trbnb.darts.R
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.models.Player
import de.trbnb.darts.models.average
import de.trbnb.darts.resources.ResourceProvider
import de.trbnb.darts.resources.ResourceValue
import de.trbnb.darts.resources.resolveAttributeAs
import de.trbnb.darts.ui.useLightOnPrimaryColor
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.DependsOn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PlayerParticipationViewModel @AssistedInject constructor(
    @Assisted private val logic: MatchLogic,
    @Assisted val player: Player,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {
    val points = logic.state.map { logic.remainingPoints(player).toString() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val isCurrentPlayer = logic.state.map { it.currentPlayer == player }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    @DependsOn("currentPlayer")
    val average: String
        get() = String.format("%.2f", logic.match[player].average)

    @DependsOn("currentPlayer")
    val wonSets: String
        get() = logic.match[player].wonSets.toString()

    @DependsOn("currentPlayer")
    val wonLegs: String
        get() = logic.currentParticipation(player).first.wonLegs.toString()

    @DependsOn("currentPlayer")
    @get:ColorInt
    val backgroundColor: Int
        get() = when (isCurrentPlayer.value) {
            true -> player.color
            false -> Color.TRANSPARENT
        }

    @DependsOn("backgroundColor")
    @get:ColorRes
    val foregroundColorRes: Int
        get() = when (isCurrentPlayer.value) {
            false -> resourceProvider.resolveAttributeAs<ResourceValue.Text>(android.R.attr.textColorPrimary).resourceId
            true -> when (player.color.useLightOnPrimaryColor()) {
                true -> R.color.white
                false -> R.color.black
            }
        }

    val showSets = logic.match.matchOptions.sets > 1

    @AssistedFactory
    interface Factory {
        operator fun invoke(logic: MatchLogic, player: Player): PlayerParticipationViewModel
    }
}