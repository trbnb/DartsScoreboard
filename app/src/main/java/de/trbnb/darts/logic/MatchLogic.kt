package de.trbnb.darts.logic

import de.trbnb.darts.logic.finish.FinishSuggestionLogic
import de.trbnb.darts.models.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MatchLogic {
    val match: Match
    val finishSuggestionLogic: FinishSuggestionLogic

    val playerOrder: StateFlow<List<Player>>
    val currentPlayer: StateFlow<Player>
    val turn: Flow<Turn>
    val turnState: Flow<TurnState>
    val remainingPoints: Flow<Int>
    val suggestedFinish: Flow<List<PotentialThrow>?>
    val suggestedFinishes: Flow<List<List<PotentialThrow>>>

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
}