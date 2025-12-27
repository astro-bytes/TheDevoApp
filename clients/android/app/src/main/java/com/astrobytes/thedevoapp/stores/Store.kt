package com.astrobytes.thedevoapp.stores

import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.models.Tap
import com.astrobytes.thedevoapp.models.User
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface TapStore {
    suspend fun put(tap: Tap): Unit
}

interface UserStore {
    suspend fun fetch(): User?
}

interface DevotionalStore {
    @OptIn(ExperimentalTime::class)
    suspend fun fetch(instant: Instant): Devotional?

    suspend fun fetch(id: Int): Devotional?
    suspend fun fetch(): List<Devotional>
}