package de.trbnb.darts.logic

import de.trbnb.darts.models.MatchOptions
import de.trbnb.darts.models.Player
import kotlinx.coroutines.flow.StateFlow

interface MatchFactory {
    val currentMatch: StateFlow<MatchLogic?>

    fun newMatch(players: List<Player>, matchOptions: MatchOptions): MatchLogic
}
