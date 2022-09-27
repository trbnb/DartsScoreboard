package de.trbnb.darts.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T1, T2> Flow<List<T1>>.deepMap(transform: (T1) -> T2): Flow<List<T2>> {
    return map { it.map(transform) }
}