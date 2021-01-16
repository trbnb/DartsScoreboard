package de.trbnb.darts.logic

import de.trbnb.darts.models.Match
import de.trbnb.darts.models.MatchOptions
import de.trbnb.darts.models.Player
import javax.inject.Inject

class MatchFactoryImpl @Inject constructor() : MatchFactory {
    override var currentMatch: MatchLogic? = null
    override fun newMatch(players: List<Player>, matchOptions: MatchOptions): MatchLogicImpl {
        return MatchLogicImpl(Match(players, matchOptions)).also { currentMatch = it }
    }
}