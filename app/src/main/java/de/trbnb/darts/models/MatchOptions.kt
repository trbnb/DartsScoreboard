package de.trbnb.darts.models

data class MatchOptions(
    val points: Int,
    val sets: Int,
    val legs: Int,
    val inRule: InOutRule,
    val outRule: InOutRule,
    val playerStartOrder: PlayerStartOrder,
    val playerOrder: PlayerOrder
)

