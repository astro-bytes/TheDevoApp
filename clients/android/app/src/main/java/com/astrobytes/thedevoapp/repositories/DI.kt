package com.astrobytes.thedevoapp.repositories

import com.astrobytes.thedevoapp.repositories.core.CoreDevotionalRepository
import com.astrobytes.thedevoapp.repositories.core.CoreUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: CoreUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindDevotionalRepository(impl: CoreDevotionalRepository): DevotionalRepository
}