package de.trbnb.darts.app

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.trbnb.darts.domain.DomainModule

@InstallIn(SingletonComponent::class)
@Module(includes = [DomainModule::class])
interface SingletonBindings