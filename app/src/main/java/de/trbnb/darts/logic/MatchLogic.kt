package de.trbnb.darts.logic

import de.trbnb.darts.logic.finish.FinishSuggestionLogic
import de.trbnb.darts.models.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.StateFlow

interface MatchLogic {
    val match: Match
    val finishSuggestionLogic: FinishSuggestionLogic

    val state: StateFlow<State>

    val gameEnded: Deferred<Unit>

    fun addThrow(_throw: Throw)
    fun fellOff(throwNumber: ThrowNumber, fellOff: Boolean)
    fun undoThrow()
    fun removeThrow(throwNumber: ThrowNumber)
    fun confirmTurn()

    fun canUndoTurnConfirmation(): Boolean
    fun undoTurnConfirmation()

    fun currentParticipation(player: Player): Pair<SetParticipation, LegParticipation>
    fun remainingPoints(player: Player): Int

    interface State {
        val match: Match
        val playerOrder: List<Player>
        val currentParticipationStats: List<CurrentParticipationStats>
        val currentPlayer: Player
        val currentTurn: Turn
        val remainingPoints: Int
        val turnState: TurnState
        val suggestedFinishes: List<List<PotentialThrow>>?
    }

    interface CurrentParticipationStats {
        val player: Player
        val matchParticipation: MatchParticipation
        val currentSet: SetParticipation
        val currentLeg: LegParticipation
        val remainingPoints: Int
    }
}