package com.astrobytes.thedevoapp.authentication

import com.astrobytes.thedevoapp.di.ApplicationScope
import com.astrobytes.thedevoapp.repositories.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
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
    private val client: SupabaseClient,
    @ApplicationScope private val scope: CoroutineScope
) : AuthProvider {

    private val _state = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    override val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        observeAuthState()
        observeUserLifecycle()
    }

    private fun observeAuthState() {
        scope.launch(Dispatchers.IO) {
            client.auth.sessionStatus
                .map { status ->
                    when (status) {
                        is SessionStatus.Authenticated ->
                            AuthState.Authenticated

                        is SessionStatus.RefreshFailure,
                        is SessionStatus.NotAuthenticated ->
                            AuthState.NotAuthenticated

                        else ->
                            _state.value // ignore transient states
                    }
                }
                .distinctUntilChanged()
                .collect { newState ->
                    _state.value = newState
                }
        }
    }

    private fun observeUserLifecycle() {
        scope.launch(Dispatchers.IO) {
            state
                .collect { authState ->
                    when (authState) {
                        AuthState.Authenticated ->
                            userRepository.refresh()

                        AuthState.NotAuthenticated ->
                            userRepository.clear()
                    }
                }
        }
    }

    override suspend fun login(): Result<Unit> =
        runCatching {
            if (state.value == AuthState.NotAuthenticated) {
                client.auth.signInAnonymously()
            }
        }

    override suspend fun logout(): Result<Unit> =
        runCatching {
            client.auth.signOut()
        }

    override fun currentState(): AuthState =
        state.value
}