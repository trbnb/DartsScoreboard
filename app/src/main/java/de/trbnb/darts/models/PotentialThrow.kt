package de.trbnb.darts.models

data class PotentialThrow(val field: Field, val multiplier: Multiplier)

val PotentialThrow.points get() = field.value * multiplier.value

val PotentialThrow.description: String
    get() = buildDescription(field, multiplier)