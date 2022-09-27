package de.trbnb.darts.models

enum class ThrowNumber(val number: Int) {
    ONE(1), TWO(2), THREE(3);

    companion object {
        val tripple: Triple<ThrowNumber, ThrowNumber, ThrowNumber>
            get() = Triple(ONE, TWO, THREE)
    }
}