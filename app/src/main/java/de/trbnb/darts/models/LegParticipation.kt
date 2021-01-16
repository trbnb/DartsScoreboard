package de.trbnb.darts.models

class LegParticipation {
    private val _turns: MutableList<Turn> = mutableListOf()

    var result: ParticipationResult = ParticipationResult.RUNNING

    val turns: List<Turn>
        get() = _turns

    fun addTurn(turn: Turn) {
        _turns.add(turn)
    }

    operator fun get(index: Int) = _turns[index]
}