package com.astrobytes.thedevoapp.repositories

import com.astrobytes.thedevoapp.repositories.core.CoreDevotionalListRepository
import com.astrobytes.thedevoapp.repositories.core.CoreDevotionalRepository
import com.astrobytes.thedevoapp.repositories.core.CoreLiveDevotionalRepository
import com.astrobytes.thedevoapp.repositories.core.CoreQuoteListRepository
import com.astrobytes.thedevoapp.repositories.core.CoreUserRepository
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
    abstract fun bindDevotionalRepository(impl: CoreLiveDevotionalRepository): LiveDevotionalRepository

    @Binds
    @Singleton
    abstract fun bindDevotionalListRepository(impl: CoreDevotionalListRepository): DevotionalListRepository

    @Binds
    @Singleton
    abstract fun bindQuoteListRepository(impl: CoreQuoteListRepository): QuoteListRepository
}

@AssistedFactory
interface DevotionalRepositoryFactory {
    fun create(id: Int): CoreDevotionalRepository
}
