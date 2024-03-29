package de.trbnb.darts.ui.match

import androidx.annotation.ColorInt
import androidx.databinding.Bindable
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import de.trbnb.darts.R
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.models.Player
import de.trbnb.darts.models.average
import de.trbnb.darts.resources.ResourceProvider
import de.trbnb.darts.ui.useLightOnPrimaryColor
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class PlayerParticipationViewModel @AssistedInject constructor(
    @Assisted private val logic: MatchLogic,
    @Assisted val player: Player,
    resourceProvider: ResourceProvider
) : BaseViewModel(), CoroutineViewModel {
    @get:Bindable
    val points by logic.currentPlayer
        .combine(logic.turn) { player, turn -> player to turn}
        .map { logic.remainingPoints(player).toString() }
        .toBindable()

    @get:Bindable
    val isCurrentPlayer by logic.currentPlayer
        .combine(logic.turn) { player, turn -> player to turn}
        .map { (currentPlayer, _) -> currentPlayer == player }
        .toBindable(defaultValue = false)

    @get:Bindable("currentPlayer")
    val average: String
        get() = "Ø " + String.format("%.2f", logic.match[player].average)

    @get:Bindable("currentPlayer")
    val wonSets: String
        get() = logic.match[player].wonSets.toString()

    @get:Bindable("currentPlayer")
    val wonLegs: String
        get() = logic.currentParticipation(player).first.wonLegs.toString()

    @get:ColorInt
    val foregroundColor: Int = when (player.color.useLightOnPrimaryColor()) {
        true -> R.color.white
        false -> R.color.black
    }.let(resourceProvider::getColor)

    @AssistedInject.Factory
    interface Factory {
        operator fun invoke(logic: MatchLogic, player: Player): PlayerParticipationViewModel
    }
}