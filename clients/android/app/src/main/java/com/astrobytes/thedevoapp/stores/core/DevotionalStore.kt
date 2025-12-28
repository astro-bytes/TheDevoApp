package com.astrobytes.thedevoapp.stores.core

import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.models.Music
import com.astrobytes.thedevoapp.models.Person
import com.astrobytes.thedevoapp.models.Quote
import com.astrobytes.thedevoapp.models.Scripture
import com.astrobytes.thedevoapp.stores.DevotionalStore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.ktor.http.Url
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SupabaseDevotionalStore @Inject constructor(
    private val client: SupabaseClient
): DevotionalStore {

    private val table = "devotional_list_view"

    @Serializable data class SupabaseDevotional @OptIn(ExperimentalTime::class) constructor(
        val id: Int,
        val startDate: Instant,
        val endDate: Instant,
        val title: String,
        val prelude: SupabaseMusic?,
        val invocation: SupabasePerson?,
        val introit: SupabaseMusic?,
        val scripture: SupabaseScripture?,
        val speaker: SupabasePerson,
        val postlude: SupabaseMusic?,
        val benediction: SupabasePerson?,
        val recessional: SupabaseMusic?,
        val topics: List<String>,
        val quotes: List<SupabaseQuote>,
        val summary: String?,
        val transcript: String?,
        val url: Url?
    ) {
        @OptIn(ExperimentalTime::class)
        fun asDevotional(): Devotional = Devotional(
            id,
            startDate,
            endDate,
            title,
            prelude?.asMusic(),
            invocation?.asPerson(),
            introit?.asMusic(),
            scripture?.asScripture(),
            speaker.asPerson(),
            postlude?.asMusic(),
            benediction?.asPerson(),
            recessional?.asMusic(),
            topics,
            quotes.map { it.asQuote() },
            summary,
            transcript,
            url
        )
    }

    @Serializable data class SupabasePerson(
        val firstName: String,
        val middleInitial: String?,
        val lastName: String
    ) {
        fun asPerson(): Person = Person(firstName, middleInitial, lastName)
    }

    @Serializable data class SupabaseMusic(
        val title: String,
        val composer: String?,
        val arranger: String?,
        val performer: SupabasePerson
    ) {
        fun asMusic(): Music = Music(title, composer, arranger, performer.asPerson())
    }

    @Serializable data class SupabaseScripture(
        val reader: SupabasePerson,
        val book: String,
        val chapter: String,
        val verses: List<Int>,
        val url: Url
    ) {
        fun asScripture(): Scripture = Scripture(reader.asPerson(), book, chapter, verses, url)
    }

    @Serializable data class SupabaseQuote(
        val id: Int,
        val devotionalId: Int,
        val likes: Int,
        val taps: Int,
        val text: String
    ) {
        fun asQuote(): Quote = Quote(id, devotionalId, likes, taps, text)
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

    override suspend fun fetch(id: Int): Devotional? = client
        .from(table)
        .select {
            filter {
                eq("id", id)
            }
            limit(1)
        }
        .decodeSingleOrNull<SupabaseDevotional>()
        ?.asDevotional()
}