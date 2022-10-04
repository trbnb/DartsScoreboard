package de.trbnb.darts.domain.models

import de.trbnb.darts.persistence.database.models.Player

class Match(val players: List<Player>, val matchOptions: MatchOptions) {
    val participations = players.map { MatchParticipation(it) }

    operator fun get(player: Player) = participations.first { it.player == player }
}