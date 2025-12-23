package com.astrobytes.thedevoapp.repositories.core

import com.astrobytes.thedevoapp.models.User
import com.astrobytes.thedevoapp.repositories.CoreRepository
import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.stores.UserStore
import javax.inject.Inject

class CoreUserRepository @Inject constructor(
    private val store: UserStore
) : CoreRepository<User>(), UserRepository {
    override suspend fun refresh(): Result<User> {
        val result = store.fetch()
        result.onSuccess { user ->
            _value.value = user
        }
        return result
    }
}
