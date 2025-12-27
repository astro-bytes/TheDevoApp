package com.astrobytes.thedevoapp.repositories

import androidx.annotation.CheckResult
import com.astrobytes.thedevoapp.models.Devotional
import com.astrobytes.thedevoapp.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A read-only repository that exposes a stream of values and operations to
 * load and clear its current state.
 *
 * Implementations are expected to:
 * - Emit updates through [value] whenever the underlying data changes.
 * - Provide the most recently loaded value via [current], if any.
 * - Load or refresh the underlying data source when [refresh] is invoked.
 * - Reset the in-memory state when [clear] is called.
 *
 * Typical usage:
 * - UI layer collects [value] to react to changes.
 * - A refresh is triggered explicitly (e.g. on screen start or pull-to-refresh).
 * - Callers handle [Result] failures from [current] and [refresh] to surface errors.
 */
interface Repository<Value> {
    /**
     * A cold [Flow] that emits non-null values when the repository state changes.
     *
     * Consumers should collect this flow to observe updates. The exact emission
     * behavior (e.g. when the first value is produced) is defined by the implementation.
     */
    val value: Flow<Value>
    /**
     * Returns the most recently loaded value, if present.
     *
     * @return [Result.success] with the current value when it has been loaded,
     * or [Result.failure] when no value is available or an error occurred.
     */
    @CheckResult
    fun current(): Value
    /**
     * Refreshes the underlying data and updates the repository state.
     *
     * Implementations typically perform I/O or long-running work and should
     * be called from a coroutine context.
     *
     * @return [Result.success] with the freshly loaded value, or [Result.failure]
     * when the refresh fails.
     */
    suspend fun refresh(): Value
    /**
     * Clears the currently held value and any in-memory state.
     *
     * After calling this, [current] is expected to return a failing [Result]
     * until a subsequent successful [refresh].
     */
    fun clear()
}

/**
 * Base implementation of [Repository] backed by a [MutableStateFlow].
 *
 * This class manages storage of the latest value and exposes it as a non-null
 * [Flow] via [value]. Subclasses are responsible for defining how data is
 * loaded and refreshed by implementing [refresh].
 *
 * Lifecycle expectations:
 * - Before any successful [refresh] call, [current] will return a failing [Result].
 * - A successful [refresh] should update [_value], which in turn updates [value].
 * - [clear] resets [_value] to `null`, causing [current] to fail again until
 *   the next successful [refresh].
 *
 * The internal [CoroutineScope] uses [Dispatchers.IO] with a [SupervisorJob]
 * and is intended for use by subclasses to launch background work if needed.
 */
abstract class CoreRepository<Value>(initialValue: Value) : Repository<Value> {
    protected val _value = MutableStateFlow<Value>(initialValue)
    override val value: Flow<Value> = _value.asStateFlow()

    override fun current(): Value {
        return _value.value
    }

    abstract override fun clear()

    // Subclasses must implement how to fetch
    abstract override suspend fun refresh(): Value
}

interface UserRepository : Repository<User?>
interface LiveDevotionalRepository: Repository<Devotional?>
interface DevotionalListRepository : Repository<List<Devotional>>
interface DevotionalRepository : Repository<Devotional?>
