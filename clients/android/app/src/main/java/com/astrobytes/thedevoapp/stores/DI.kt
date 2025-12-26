package com.astrobytes.thedevoapp.stores

import com.astrobytes.thedevoapp.stores.core.SupabaseDevotionalStore
import com.astrobytes.thedevoapp.stores.core.SupabaseTapStore
import com.astrobytes.thedevoapp.stores.core.SupabaseUserStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreModule {
    @Binds
    abstract fun bindTapStore(impl: SupabaseTapStore): TapStore

    @Binds
    abstract fun bindUserStore(impl: SupabaseUserStore): UserStore

    @Binds
    abstract fun bindDevotionalStore(impl: SupabaseDevotionalStore): DevotionalStore
}