package de.trbnb.darts.models

data class Turn(val first: Throw? = null, val second: Throw? = null, val third: Throw? = null)

operator fun Turn.get(number: ThrowNumber): Throw? = when (number) {
    ThrowNumber.ONE -> first
    ThrowNumber.TWO -> second
    ThrowNumber.THREE -> third
}

val Turn.value: Int
    get() = first.value + second.value + third.value