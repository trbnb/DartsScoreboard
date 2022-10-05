package de.trbnb.darts.domain.models

import java.util.UUID

data class Player(
    val id: UUID,
    val name: String,
    val color: ULong
)