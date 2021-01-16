package de.trbnb.darts.models

enum class InOutRule(vararg val multipliers: Multiplier) {
    STRAIGHT(*Multiplier.values()),
    DOUBLE(Multiplier.DOUBLE),
    TRIPLE(Multiplier.TRIPLE),
    MASTER(Multiplier.DOUBLE, Multiplier.TRIPLE)
}
