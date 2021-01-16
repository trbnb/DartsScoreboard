package de.trbnb.darts.models

data class Throw(val field: Field, val multiplier: Multiplier, val state: ThrowState = ThrowState.OK)

val Throw?.value: Int
    get() = when (this) {
        null -> 0
        else -> when(state) {
            ThrowState.OK -> field.value * multiplier.value
            else -> 0
        }
    }

val Throw.description: String
    get() = buildDescription(field, multiplier)