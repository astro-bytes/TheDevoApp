package com.astrobytes.thedevoapp.usecases

import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.models.Tap
import com.astrobytes.thedevoapp.repositories.LiveDevotionalRepository
import com.astrobytes.thedevoapp.stores.TapStore
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class RecordTap @Inject constructor(
    private val tapStore: TapStore,
    private val userRepository: UserRepository,
    private val devotionalRepository: LiveDevotionalRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend fun execute() {
        val instant: Instant = Clock.System.now()
        val user = userRepository.current() ?: return
        var devotional = devotionalRepository.current() ?: devotionalRepository.refresh() ?: return
        val tap = Tap(devotional.id, user.id, instant)
        tapStore.put(tap)
    }
}