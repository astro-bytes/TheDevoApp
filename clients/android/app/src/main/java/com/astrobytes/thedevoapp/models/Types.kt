package com.astrobytes.thedevoapp.models

import io.ktor.http.Url
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class User (val id: String)

data class Tap @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val devotionalId: Int,
    val userId: String,
    val timestamp: Instant
)

// TODO: Remove the default values in the constructor
data class Devotional @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val startDate: Instant,
    val endDate: Instant,
    val title: String = "The Devotional Title",
    val prelude: Music? = Music(),
    val invocation: String? = "Janice",
    val introit: Music? = Music(),
    val scripture: Scripture? = Scripture(),
    val speaker: Speaker = Speaker(),
    val postlude: Music? = Music(),
    val benediction: String? = "Johnny",
    val recessional: Music? = Music(),
    val topics: List<String> = listOf("Faith", "Prayer", "Love"),
    val quotes: List<Quote> = listOf(Quote()),
    val summary: String? = "The best way to predict the future is to create it.",
    val transcript: String? = "The best way to predict the future is to create it. The best way to predict the future is to create it.",
    val url: Url? = Url("https://google.com")
)

data class Music(
    val id: Int = 1,
    val title: String = "Unknown Song",
    val composer: String = "Unknown Composer"
)

data class Scripture(
    val id: Int = 1,
    val book: String = "1 Nephi",
    val chapter: String = "5",
    val verses: List<String> = listOf("7"),
    val url: Url? = Url("https://google.com")
)

// TODO: Remove the default values in the constructor
data class Speaker(
    val id: Int = 1,
    val firstName: String = "John",
    val lastName: String = "Doe",
    val profession: String? = "Speaker"
) {
    val fullName: String
        get() = "$firstName $lastName"
}

// TODO: Remove the default values in the constructor
data class Quote(
    val id: Int = 1,
    val devotionalId: Int = 1,
    val likes: Int = 0,
    val taps: Int = 0,
    val text: String = "The best way to predict the future is to create it."
)