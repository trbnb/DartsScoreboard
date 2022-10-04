package de.trbnb.darts.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck
import de.trbnb.darts.domain.logic.MatchFactory
import de.trbnb.darts.domain.logic.MatchFactoryImpl
import de.trbnb.darts.domain.players.PlayerRepository
import de.trbnb.darts.domain.players.PlayerRepositoryImpl
import de.trbnb.darts.domain.vibration.Vibrator
import de.trbnb.darts.domain.vibration.VibratorImpl
import de.trbnb.darts.persistence.PersistenceModule
import javax.inject.Singleton

@DisableInstallInCheck
@Module(includes = [PersistenceModule::class])
interface DomainModule {
    @Binds
    @Singleton
    fun matchFactory(impl: MatchFactoryImpl): MatchFactory

    @Binds
    @Singleton
    fun vibrator(impl: VibratorImpl): Vibrator

    @Binds
    @Singleton
    fun playerRepository(impl: PlayerRepositoryImpl): PlayerRepository
}