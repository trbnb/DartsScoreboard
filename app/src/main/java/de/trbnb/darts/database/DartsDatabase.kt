package de.trbnb.darts.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.trbnb.darts.database.converters.UuidConverters
import de.trbnb.darts.database.daos.PlayerDao
import de.trbnb.darts.models.Player

@Database(entities = [Player::class], version = 1)
@TypeConverters(UuidConverters::class)
abstract class DartsDatabase : RoomDatabase() {
    abstract val playerDao: PlayerDao
}
