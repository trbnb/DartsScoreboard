package de.trbnb.darts.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.trbnb.darts.database.DartsDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SingletonModule {
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
