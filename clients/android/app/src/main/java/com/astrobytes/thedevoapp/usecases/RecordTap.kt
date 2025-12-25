package com.astrobytes.thedevoapp.usecases

import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.models.Tap
import com.astrobytes.thedevoapp.stores.TapStore
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class RecordTap @Inject constructor(private val tapStore: TapStore, private val userRepo: UserRepository) {
    @OptIn(ExperimentalTime::class)
    suspend fun execute(devotionalId: Int): Result<Unit> = runCatching {
        val user = userRepo.current()
        val id = -1
        user?.let {
            val tap = Tap(id, devotionalId, it.id)
            tapStore.put(tap)
        }
    }
}