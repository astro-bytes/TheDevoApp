package com.astrobytes.thedevoapp.repositories.core

import com.astrobytes.thedevoapp.models.User
import com.astrobytes.thedevoapp.repositories.CoreRepository
import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.stores.UserStore
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CoreUserRepository @Inject constructor(
    private val store: UserStore
) : CoreRepository<User?>(null), UserRepository {
    override suspend fun refresh(): User? {
        val user = store.fetch()
        _value.update { user }
        return user
    }

    override fun clear() {
        _value.update { null }
    }
}
