package de.trbnb.darts.database.daos

import androidx.room.*
import de.trbnb.darts.models.Player
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
}