package de.trbnb.darts.domain.utils

typealias Triple<T> = kotlin.Triple<T, T, T>

fun <T1, T2> Triple<T1>.map(transform: (T1) -> T2): Triple<T2> {
    return Triple(
        transform(first),
        transform(second),
        transform(third)
    )
}

inline fun <T> Triple<T>.forEach(action: (T) -> Unit) {
    action(first)
    action(second)
    action(third)
}