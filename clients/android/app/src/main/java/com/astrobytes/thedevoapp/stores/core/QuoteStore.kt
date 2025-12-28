package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.Quote
import com.astrobytes.thedevoapp.stores.QuoteStore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class SupabaseQuoteStore @Inject constructor(
    private val client: SupabaseClient
): QuoteStore {
    @Serializable
    data class SupabaseQuote(
        val id: Int,
        @SerialName("devotional_id") val devotionalId: Int,
        @SerialName("like_count") val likes: Int,
        @SerialName("tap_count") val taps: Int,
        @SerialName("blob") val text: String
    ) {
        fun asQuote(): Quote = Quote(id, devotionalId, likes, taps, text)
    }

    private val table = "quotes"
    override suspend fun fetch(): List<Quote> = client
        .from(table)
        .select()
        .decodeList<SupabaseQuote>()
        .map { it.asQuote() }
}