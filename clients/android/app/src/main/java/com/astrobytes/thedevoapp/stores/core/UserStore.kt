package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import com.astrobytes.thedevoapp.stores.UserStore
import javax.inject.Inject

class SupabaseUserStore @Inject constructor(private val client: SupabaseClient): UserStore {
    override suspend fun fetch(): User? {
        val user = client.auth.currentUserOrNull()
        user?.let { return User(it.id) }
        return null
    }
}