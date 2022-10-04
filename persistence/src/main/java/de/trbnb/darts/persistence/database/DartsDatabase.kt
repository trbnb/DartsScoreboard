package de.trbnb.darts.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.trbnb.darts.persistence.database.converters.UuidConverters
import de.trbnb.darts.persistence.database.daos.PlayerDao
import de.trbnb.darts.persistence.database.models.Player

@Database(entities = [Player::class], version = 1)
@TypeConverters(UuidConverters::class)
abstract class DartsDatabase : RoomDatabase() {
    abstract val playerDao: PlayerDao
}
