package com.astrobytes.thedevoapp.stores

import com.astrobytes.thedevoapp.models.Tap
import com.astrobytes.thedevoapp.models.User

interface TapStore {
    suspend fun put(tap: Tap): Unit
}

interface UserStore {
    suspend fun fetch(): User?
}