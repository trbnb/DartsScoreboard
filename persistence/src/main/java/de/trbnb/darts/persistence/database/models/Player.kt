package de.trbnb.darts.persistence.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Player(
    @PrimaryKey val id: UUID,
    val name: String,
    val color: Long
)