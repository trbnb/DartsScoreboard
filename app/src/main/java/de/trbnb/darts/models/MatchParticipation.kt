package de.trbnb.darts.models

class MatchParticipation(val player: Player) {
    private val _sets: MutableList<SetParticipation> = mutableListOf(SetParticipation())

    var result: ParticipationResult = ParticipationResult.RUNNING

    val sets: List<SetParticipation>
        get() = _sets

    val wonSets: Int
        get() = _sets.count { it.result == ParticipationResult.WON }

    fun addSet() {
        _sets.add(SetParticipation())
    }

    operator fun get(index: Int) = _sets[index]
    operator fun set(index: Int, setParticipation: SetParticipation) { _sets[index] = setParticipation }
}

val MatchParticipation.average: Double
    get() = sets.asSequence()
        .flatMap { it.legs }
        .flatMap { it.turns }
        .filterNot { it.first == null && it.second == null && it.third == null }
        .map { turn ->
            if (turn.first != null && turn.second != null && turn.third != null) {
                turn.value.toDouble()
            } else {
                listOfNotNull(turn.first, turn.second, turn.third).map { it.value }.average() * 3
            }
        }.average().takeUnless { it.isNaN() } ?: 0.0