package com.astrobytes.thedevoapp.repositories

import androidx.annotation.CheckResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

interface Repository<Value> {
    val value: Flow<Value>
    @CheckResult
    fun current(): Result<Value>
    suspend fun refresh(): Result<Value>
    fun clear()
}

abstract class CoreRepository<Value> : Repository<Value> {
    protected val _value = MutableStateFlow<Value?>(null)
    override val value: Flow<Value> = _value.filterNotNull()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun current(): Result<Value> =
        _value.value?.let { Result.success(it) } ?: Result.failure(IllegalStateException("Value not loaded"))

    override fun clear() {
        _value.value = null
    }

    // Subclasses must implement how to fetch
    override abstract suspend fun refresh(): Result<Value>
}