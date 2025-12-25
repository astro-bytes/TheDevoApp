package com.astrobytes.thedevoapp.models

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User (val id: String)

data class Tap @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val devotionalId: Int,
    val userId: String,
    val timestamp: Instant = Clock.System.now()
)