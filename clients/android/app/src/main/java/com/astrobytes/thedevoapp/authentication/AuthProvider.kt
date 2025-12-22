package com.astrobytes.thedevoapp.authentication

import com.astrobytes.thedevoapp.repositories.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthState {
    Authenticated,
    NotAuthenticated
}

interface AuthProvider {
    val state: Flow<AuthState>
    suspend fun login(): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun currentState(): AuthState
}

class SupabaseAuthProvider @Inject constructor(
    private val userRepository: UserRepository,
    private val client: SupabaseClient
) : AuthProvider {

    private val _state = MutableStateFlow<AuthState?>(null)
    override val state: Flow<AuthState> = _state.filterNotNull()

    // A scope tied to this class
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            client.auth.sessionStatus.collect { sessionStatus ->
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        _state.value = AuthState.Authenticated
                    }
                    is SessionStatus.RefreshFailure -> {
                        _state.value = AuthState.NotAuthenticated
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _state.value = AuthState.NotAuthenticated
                    }
                    else -> {
                        // TODO: Determine if something is needed to be done.
                        // https://supabase.com/docs/reference/kotlin/auth-onauthstatechange
                    }
                }

                userRepository.refresh()
            }
        }
    }

    override suspend fun login(): Result<Unit> = runCatching {
        if (_state.value == AuthState.NotAuthenticated) {
            client.auth.signInAnonymously()
        }
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        client.auth.signOut()
    }

    override fun currentState(): AuthState {
        return _state.value ?: AuthState.NotAuthenticated
    }
}