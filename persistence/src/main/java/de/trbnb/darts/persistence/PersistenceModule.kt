package de.trbnb.darts.persistence

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.migration.DisableInstallInCheck
import de.trbnb.darts.persistence.database.DartsDatabase
import javax.inject.Singleton

@DisableInstallInCheck
@Module
class PersistenceModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): DartsDatabase {
        return Room.databaseBuilder(context, DartsDatabase::class.java, "darts")
            .build()
    }

    @Provides
    @Singleton
    fun playerDao(database: DartsDatabase) = database.playerDao
}