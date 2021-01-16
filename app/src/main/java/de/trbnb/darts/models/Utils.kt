package de.trbnb.darts.models

fun buildDescription(field: Field, multiplier: Multiplier) = when (field) {
    Field.MISS -> "MISS"
    Field.BULL -> when (multiplier) {
        Multiplier.DOUBLE -> "BULL"
        else -> field.value.toString()
    }
    else -> {
        val prefix = when (multiplier) {
            Multiplier.SINGLE -> ""
            Multiplier.DOUBLE -> "D"
            Multiplier.TRIPLE -> "T"
        }

        "$prefix${field.value}"
    }
}