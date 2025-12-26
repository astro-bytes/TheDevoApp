package com.astrobytes.thedevoapp.models

import com.astrobytes.thedevoapp.stores.core.SupabaseDevotionalStore.SupabaseDevotional
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User (val id: String)

data class Tap @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val devotionalId: Int,
    val userId: String,
    val timestamp: Instant
)

data class Devotional @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val startDate: Instant,
    val endDate: Instant
)