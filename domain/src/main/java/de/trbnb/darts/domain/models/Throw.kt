package de.trbnb.darts.domain.models

data class Throw(val field: Field, val multiplier: Multiplier, val state: ThrowState = ThrowState.OK)

val Throw?.value: Int
    get() = when (this) {
        null -> 0
        else -> when(state) {
            ThrowState.OK -> rawValue
            else -> 0
        }
    }

val Throw.rawValue: Int get() = field.value * multiplier.value

val Throw.description: String
    get() = buildDescription(field, multiplier)