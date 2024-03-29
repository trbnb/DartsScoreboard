package de.trbnb.darts.database.converters

import androidx.room.TypeConverter
import java.util.*

class UuidConverters {
    @TypeConverter fun UUID.serialize(): String = toString()
    @TypeConverter fun String.deserialize(): UUID = UUID.fromString(this)
}