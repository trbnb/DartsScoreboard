package de.trbnb.darts.domain.models

class SetParticipation {
    private val _legs: MutableList<LegParticipation> = mutableListOf(LegParticipation())

    var result: ParticipationResult = ParticipationResult.RUNNING

    val legs: List<LegParticipation>
        get() = _legs

    val wonLegs: Int
        get() = _legs.count { it.result == ParticipationResult.WON }

    fun addLeg() {
        _legs.add(LegParticipation())
    }

    operator fun get(index: Int) = _legs[index]
    operator fun set(index: Int, legParticipation: LegParticipation) { _legs[index] = legParticipation }
}