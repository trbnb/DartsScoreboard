package de.trbnb.darts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.trbnb.darts.logic.MatchFactory
import de.trbnb.darts.logic.MatchFactoryImpl
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.darts.players.PlayerRepositoryImpl
import de.trbnb.darts.resources.ContextResourceProvider
import de.trbnb.darts.resources.ResourceProvider
import de.trbnb.darts.vibration.Vibrator
import de.trbnb.darts.vibration.VibratorImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface SingletonBindings {
    @Binds
    @Singleton
    fun matchFactory(impl: MatchFactoryImpl): MatchFactory

    @Binds
    @Singleton
    fun vibrator(impl: VibratorImpl): Vibrator

    @Binds
    @Singleton
    fun resourceProvider(impl: ContextResourceProvider): ResourceProvider

    @Binds
    @Singleton
    fun playerRepository(impl: PlayerRepositoryImpl): PlayerRepository
}