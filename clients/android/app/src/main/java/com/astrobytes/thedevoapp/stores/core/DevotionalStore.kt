package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.stores.DevotionalStore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SupabaseDevotionalStore @Inject constructor(
    private val client: SupabaseClient
): DevotionalStore {

    private val table = "devotionals"

    @Serializable data class SupabaseDevotional @OptIn(ExperimentalTime::class) constructor(
        @SerialName("id") val id: Int,
        @SerialName("date_started") val startDate: Instant,
        @SerialName("date_ended") val endDate: Instant
    ) {
        @OptIn(ExperimentalTime::class)
        fun asDevotional(): Devotional = Devotional(id, startDate, endDate)
    }

    override suspend fun fetch(): List<Devotional> = client
        .from(table)
        .select()
        .decodeList<SupabaseDevotional>()
        .map { it.asDevotional() }

    @OptIn(ExperimentalTime::class)
    override suspend fun fetch(instant: Instant): Devotional? = client
        .from(table)
        .select {
            filter {
                lte("date_started", instant)
                gte("date_ended", instant)
            }
            limit(1)
        }
        .decodeSingleOrNull<SupabaseDevotional>()
        ?.asDevotional()
}