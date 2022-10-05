package de.trbnb.darts.domain.players

import de.trbnb.darts.domain.models.Player
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PlayerRepository {
    fun getAll(): Flow<List<Player>>
    suspend fun getById(id: UUID): Player?
    suspend fun getByIds(ids: List<UUID>): List<Player>

    suspend fun create(name: String, color: ULong)
    suspend fun delete(player: Player)
    suspend fun update(player: Player)
}
