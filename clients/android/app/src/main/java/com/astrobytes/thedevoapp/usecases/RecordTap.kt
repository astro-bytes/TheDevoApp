package com.astrobytes.thedevoapp.usecases

import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.models.Tap
import com.astrobytes.thedevoapp.stores.TapStore
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class RecordTap @Inject constructor(private val tapStore: TapStore, private val userRepo: UserRepository) {
    @OptIn(ExperimentalTime::class)
    suspend fun execute(devotionalId: Int) {
        userRepo.current()?.let {
            val tap = Tap(-1, devotionalId, it.id)
            tapStore.put(tap)
        }
    }
}