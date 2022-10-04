package de.trbnb.darts.domain.models

enum class Field(val value: Int, vararg val multipliers: Multiplier = Multiplier.values()) {
    MISS(0, Multiplier.SINGLE),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15),
    SIXTEEN(16),
    SEVENTEEN(17),
    EIGHTEEN(18),
    NINETEEN(19),
    TWENTY(20),
    BULL(25, Multiplier.SINGLE, Multiplier.DOUBLE)
}

operator fun Field.plus(multiplier: Multiplier) = Throw(this, multiplier)

fun Int.toField() = Field.values().first { it.value == this }