package com.astrobytes.thedevoapp.usecases

import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.stores.core.TapStore
import com.astrobytes.thedevoapp.models.Tap
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class RecordTap @Inject constructor(private val tapStore: TapStore, private val userRepo: UserRepository) {
    @OptIn(ExperimentalTime::class)
    suspend fun execute(devotionalId: Int): Result<Unit> = runCatching {
        val result = userRepo.current()
        val id = -1
        result.onSuccess {
            val tap = Tap(id, devotionalId, it.id)
            tapStore.put(tap)
        }

    }
}