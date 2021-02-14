package de.trbnb.darts.players

import androidx.annotation.ColorInt
import de.trbnb.darts.models.Player
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PlayerRepository {
    fun getAll(): Flow<List<Player>>
    suspend fun getById(id: UUID): Player?
    suspend fun getByIds(ids: List<UUID>): List<Player>

    suspend fun create(name: String, @ColorInt color: Int)
    suspend fun delete(player: Player)
    suspend fun update(player: Player)
}
