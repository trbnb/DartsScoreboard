package de.trbnb.darts.utils

fun wontHappen(message: String = "This won't ever happen"): Nothing = throw Error(message)