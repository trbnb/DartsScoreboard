package de.trbnb.darts.domain.logic

import de.trbnb.darts.domain.models.Match
import de.trbnb.darts.domain.models.MatchOptions
import de.trbnb.darts.domain.models.Player
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MatchFactoryImpl @Inject constructor() : MatchFactory {
    override val currentMatch = MutableStateFlow<MatchLogic?>(null)

    override fun newMatch(players: List<Player>, matchOptions: MatchOptions): MatchLogicImpl {
        return MatchLogicImpl(
            Match(
                players,
                matchOptions.copy(legs = matchOptions.legs.takeUnless { it == 0 } ?: Int.MAX_VALUE)
            )
        ).also { currentMatch.value = it }
    }
}