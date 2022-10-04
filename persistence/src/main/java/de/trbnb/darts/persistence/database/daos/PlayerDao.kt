package de.trbnb.darts.persistence.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.trbnb.darts.persistence.database.models.Player
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(player: Player)

    @Delete
    suspend fun delete(player: Player)

    @Update
    suspend fun update(vararg players: Player)

    @Query("SELECT * FROM player")
    fun getAll(): Flow<List<Player>>

    @Query("SELECT * FROM player where player.id = :id")
    suspend fun getById(id: UUID): Player?

    @Query("SELECT * FROM player where player.id IN(:ids)")
    suspend fun getByIds(ids: List<UUID>): List<Player>
}