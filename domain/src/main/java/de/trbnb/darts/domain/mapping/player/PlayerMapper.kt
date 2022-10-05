package de.trbnb.darts.domain.mapping.player

import de.trbnb.darts.domain.mapping.Mapper
import de.trbnb.darts.domain.models.Player
import de.trbnb.darts.persistence.database.models.PlayerEntity
import javax.inject.Inject

typealias PlayerMapper = Mapper<PlayerEntity, Player>

class PlayerMapperImpl @Inject constructor() : PlayerMapper {
    override fun map(origin: PlayerEntity) = Player(
        id = origin.id,
        name = origin.name,
        color = origin.color.toULong()
    )

    override fun mapBack(origin: Player) = PlayerEntity(
        id = origin.id,
        name = origin.name,
        color = origin.color.toLong()
    )
}