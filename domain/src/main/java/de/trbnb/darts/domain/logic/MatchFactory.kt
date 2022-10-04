package de.trbnb.darts.domain.logic

import de.trbnb.darts.domain.models.MatchOptions
import de.trbnb.darts.persistence.database.models.Player
import kotlinx.coroutines.flow.StateFlow

interface MatchFactory {
    val currentMatch: StateFlow<MatchLogic?>

    fun newMatch(players: List<Player>, matchOptions: MatchOptions): MatchLogic
}
