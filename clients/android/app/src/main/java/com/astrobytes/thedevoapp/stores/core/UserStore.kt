package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import java.lang.IllegalStateException
import javax.inject.Inject

class SupabaseUserStore @Inject constructor(private val client: SupabaseClient): UserStore {
    override suspend fun fetch(): Result<User> = runCatching {
        val user = client.auth.currentUserOrNull()
            ?: throw IllegalStateException("User is not logged in")
        User(user.id)
    }
}