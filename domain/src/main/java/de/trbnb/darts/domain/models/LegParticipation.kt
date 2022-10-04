package de.trbnb.darts.domain.models

class LegParticipation {
    private val _turns: MutableList<Turn> = mutableListOf()

    var result: ParticipationResult = ParticipationResult.RUNNING

    val turns: List<Turn>
        get() = _turns

    fun addTurn(turn: Turn) {
        _turns.add(turn)
    }

    fun removeLast(): Turn? = when {
        _turns.isEmpty() -> null
        else -> _turns.removeAt(_turns.lastIndex)
    }

    operator fun get(index: Int) = _turns[index]
}