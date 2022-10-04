package de.trbnb.darts.domain.utils

fun wontHappen(message: String = "This won't ever happen"): Nothing = throw Error(message)