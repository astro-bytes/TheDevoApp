package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.Tap
import com.astrobytes.thedevoapp.stores.TapStore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SupabaseTapStore @Inject constructor(private val client: SupabaseClient): TapStore {
    @Serializable
    data class SupabaseTap @OptIn(ExperimentalTime::class) constructor(
        @SerialName("anon_user_id") val userId: String,
        @SerialName("devotional_id") val devotionalId: Int,
        @SerialName("stamp") val timestamp: Instant
    ) {
        @OptIn(ExperimentalTime::class)
        constructor(tap: Tap) : this(tap.userId, tap.devotionalId, tap.timestamp)
    }

    override suspend fun put(tap: Tap): Unit {
        client.from("taps").insert(SupabaseTap(tap))
    }
}