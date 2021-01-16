package de.trbnb.darts.models

class Match(val players: List<Player>, val matchOptions: MatchOptions) {
    val participations = players.map { MatchParticipation(it) }

    operator fun get(player: Player) = participations.first { it.player == player }
}