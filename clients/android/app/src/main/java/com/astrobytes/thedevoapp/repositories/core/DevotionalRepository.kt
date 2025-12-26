package com.astrobytes.thedevoapp.repositories.core

import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.repositories.CoreRepository
import com.astrobytes.thedevoapp.repositories.DevotionalListRepository
import com.astrobytes.thedevoapp.repositories.DevotionalRepository
import com.astrobytes.thedevoapp.stores.DevotionalStore
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CoreDevotionalRepository @Inject constructor(
    private val store: DevotionalStore
): CoreRepository<Devotional?>(null), DevotionalRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun refresh(): Devotional? {
        val devotional = store.fetch(Clock.System.now()) ?: return null
        _value.update { devotional }
        return devotional
    }

    override fun clear() {
        _value.update { null }
    }
}

class CoreDevotionalListRepository @Inject constructor(
    private val store: DevotionalStore
): CoreRepository<List<Devotional>>(listOf()), DevotionalListRepository {
    override suspend fun refresh(): List<Devotional> {
        val devotionals = store.fetch()
        _value.update { devotionals }
        return devotionals
    }

    override fun clear() {
        _value.update { listOf() }
    }
}