package com.astrobytes.thedevoapp.models

import io.ktor.http.Url
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User (val id: String)

data class Tap @OptIn(ExperimentalTime::class) constructor(
    val devotionalId: Int,
    val userId: String,
    val timestamp: Instant
)

data class Devotional @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val startDate: Instant,
    val endDate: Instant,
    val title: String,
    val prelude: Music?,
    val invocation: Person?,
    val introit: Music?,
    val scripture: Scripture?,
    val speaker: Person,
    val postlude: Music?,
    val benediction: Person?,
    val recessional: Music?,
    val topics: List<String>,
    val quotes: List<Quote>,
    val summary: String?,
    val transcript: String?,
    val url: Url?
)

data class Music(
    val title: String,
    val composer: String?,
    val arranger: String?,
    val performer: Person
)

data class Scripture(
    val reader: Person,
    val book: String,
    val chapter: String,
    val verses: List<Int>,
    val url: Url?
)

data class Person(
    val firstName: String,
    val middleInitial: String?,
    val lastName: String
) {
    val fullName: String
        get() = "$firstName ${middleInitial?.let { "$it. " } ?: ""}$lastName"
}

data class Quote(
    val likes: Int,
    val taps: Int,
    val text: String
)