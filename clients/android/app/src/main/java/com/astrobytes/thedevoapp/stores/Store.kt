package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.User

interface UserStore {
    suspend fun fetch(): Result<User>
}