package com.astrobytes.thedevoapp.authentication

import com.astrobytes.thedevoapp.di.ApplicationScope
import com.astrobytes.thedevoapp.repositories.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthState {
    Authenticated,
    NotAuthenticated
}

interface AuthProvider {
    val value: Flow<AuthState>
    fun current() : AuthState
    suspend fun login()
    suspend fun logout()
}

class SupabaseAuthProvider @Inject constructor(
    private val userRepository: UserRepository,
    private val client: SupabaseClient,
    @ApplicationScope private val scope: CoroutineScope
) : AuthProvider {

    private val state = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    override val value: Flow<AuthState> = state.asStateFlow()

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
                            state.value // ignore transient states
                    }
                }
                .distinctUntilChanged()
                .collect { newState ->
                    state.value = newState
                }
        }
    }

    private fun observeUserLifecycle() {
        scope.launch(Dispatchers.IO) {
            var lastAuthState: AuthState? = null

            state.collect { authState ->
                if (authState != lastAuthState) {
                    when (authState) {
                        AuthState.Authenticated -> {
                            userRepository.refresh()
                        }
                        AuthState.NotAuthenticated -> {
                            userRepository.clear()
                        }
                    }
                    lastAuthState = authState
                }
            }
        }
    }


    override suspend fun login() {
        if (state.value == AuthState.NotAuthenticated) {
            client.auth.signInAnonymously()
        }
    }

    override suspend fun logout() {
        // TODO: Delete the user.
        if (state.value == AuthState.Authenticated) {
            client.auth.signOut()
        }
    }

    override fun current(): AuthState = state.value
}