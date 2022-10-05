package de.trbnb.darts.persistence.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.trbnb.darts.persistence.database.models.PlayerEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(player: PlayerEntity)

    @Delete
    suspend fun delete(player: PlayerEntity)

    @Update
    suspend fun update(vararg players: PlayerEntity)

    @Query("SELECT * FROM player")
    fun getAll(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM player where player.id = :id")
    suspend fun getById(id: UUID): PlayerEntity?

    @Query("SELECT * FROM player where player.id IN(:ids)")
    suspend fun getByIds(ids: List<UUID>): List<PlayerEntity>
}