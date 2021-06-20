package de.trbnb.darts.ui.match

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
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
import de.trbnb.mvvmbase.Bindable
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class PlayerParticipationViewModel @AssistedInject constructor(
    @Assisted private val logic: MatchLogic,
    @Assisted val player: Player,
    private val resourceProvider: ResourceProvider
) : BaseViewModel(), CoroutineViewModel {
    val points by logic.currentPlayer
        .combine(logic.turn) { player, turn -> player to turn }
        .map { logic.remainingPoints(player).toString() }
        .toBindable()

    val isCurrentPlayer by logic.currentPlayer
        .combine(logic.turn) { player, turn -> player to turn}
        .map { (currentPlayer, _) -> currentPlayer == player }
        .toBindable(defaultValue = false)

    @get:Bindable("currentPlayer")
    val average: String
        get() = String.format("%.2f", logic.match[player].average)

    @get:Bindable("currentPlayer")
    val wonSets: String
        get() = logic.match[player].wonSets.toString()

    @get:Bindable("currentPlayer")
    val wonLegs: String
        get() = logic.currentParticipation(player).first.wonLegs.toString()

    @get:Bindable("currentPlayer")
    @get:ColorInt
    val backgroundColor: Int
        get() = when (isCurrentPlayer) {
            true -> player.color
            false -> Color.TRANSPARENT
        }

    @get:Bindable("backgroundColor")
    @get:ColorRes
    val foregroundColorRes: Int
        get() = when (isCurrentPlayer) {
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