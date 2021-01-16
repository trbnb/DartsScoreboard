package de.trbnb.darts.logic

import de.trbnb.darts.models.MatchOptions
import de.trbnb.darts.models.Player

interface MatchFactory {
    val currentMatch: MatchLogic?

    fun newMatch(players: List<Player>, matchOptions: MatchOptions): MatchLogic
}
