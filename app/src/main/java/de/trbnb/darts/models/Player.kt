package de.trbnb.darts.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Player(
    @PrimaryKey val id: UUID,
    val name: String,
    val color: Long
)