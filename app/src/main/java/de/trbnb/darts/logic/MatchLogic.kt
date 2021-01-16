package de.trbnb.darts.logic

import de.trbnb.darts.logic.finish.FinishSuggestionLogic
import de.trbnb.darts.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MatchLogic {
    val match: Match
    val finishSuggestionLogic: FinishSuggestionLogic

    val currentPlayer: StateFlow<Player>
    val turn: Flow<Turn>
    val turnState: Flow<TurnState>
    val remainingPoints: Flow<Int>
    val suggestedFinish: Flow<List<PotentialThrow>?>

    fun addThrow(_throw: Throw)
    fun fellOff(throwNumber: ThrowNumber, fellOff: Boolean)
    fun undo()
    fun remove(throwNumber: ThrowNumber)
    fun confirmTurn()

    fun currentParticipation(player: Player): Pair<SetParticipation, LegParticipation>
    fun remainingPoints(player: Player): Int
}