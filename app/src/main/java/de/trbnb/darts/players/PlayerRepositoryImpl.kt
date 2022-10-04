package de.trbnb.darts.players

import de.trbnb.darts.database.daos.PlayerDao
import de.trbnb.darts.models.Player
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(private val dao: PlayerDao) : PlayerRepository {
    override fun getAll(): Flow<List<Player>> = dao.getAll()
    override suspend fun getById(id: UUID): Player? = dao.getById(id)
    override suspend fun getByIds(ids: List<UUID>): List<Player> = dao.getByIds(ids)
    override suspend fun create(name: String, color: ULong) {
        dao.insert(Player(UUID.randomUUID(), name, color.toLong()))
    }
    override suspend fun delete(player: Player) = dao.delete(player)
    override suspend fun update(player: Player) = dao.update(player)
}