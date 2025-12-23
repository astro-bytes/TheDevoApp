package com.astrobytes.thedevoapp.stores

import com.astrobytes.thedevoapp.models.User

interface UserStore {
    suspend fun fetch(): Result<User>
}