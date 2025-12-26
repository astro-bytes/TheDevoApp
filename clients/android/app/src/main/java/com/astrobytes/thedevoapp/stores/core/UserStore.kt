package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import com.astrobytes.thedevoapp.stores.UserStore
import io.github.jan.supabase.auth.user.UserInfo
import javax.inject.Inject

class SupabaseUserStore @Inject constructor(private val client: SupabaseClient): UserStore {
    override suspend fun fetch(): User? = client
        .auth
        .currentUserOrNull()
        ?.asUser()
}

private fun UserInfo.asUser(): User = User(this.id)