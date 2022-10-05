package de.trbnb.darts.domain.mapping

interface Mapper<T1, T2> {
    fun map(origin: T1): T2
    fun mapBack(origin: T2): T1
}

fun <T1, T2> Mapper<T1, T2>.mapAll(origin: List<T1>): List<T2> = origin.map(::map)
fun <T1, T2> Mapper<T1, T2>.mapBackAll(origin: List<T2>): List<T1> = origin.map(::mapBack)