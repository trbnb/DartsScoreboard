package de.trbnb.darts.domain.players

import de.trbnb.darts.domain.mapping.mapAll
import de.trbnb.darts.domain.mapping.player.PlayerMapper
import de.trbnb.darts.domain.models.Player
import de.trbnb.darts.persistence.database.daos.PlayerDao
import de.trbnb.darts.persistence.database.models.PlayerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val dao: PlayerDao,
    private val mapper: PlayerMapper
) : PlayerRepository {
    override fun getAll(): Flow<List<Player>> = dao.getAll().map { mapper.mapAll(it) }
    override suspend fun getById(id: UUID): Player? = dao.getById(id)?.let(mapper::map)
    override suspend fun getByIds(ids: List<UUID>): List<Player> {
        return dao.getByIds(ids).let { mapper.mapAll(it) }
    }

    override suspend fun create(name: String, color: ULong) {
        dao.insert(
            PlayerEntity(
                UUID.randomUUID(),
                name,
                color.toLong()
            )
        )
    }
    override suspend fun delete(player: Player) = dao.delete(player.let(mapper::mapBack))
    override suspend fun update(player: Player) = dao.update(player.let(mapper::mapBack))
}