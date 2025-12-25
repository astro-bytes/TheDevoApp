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
    NotAuthenticated,
    Initializing
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

    private val state = MutableStateFlow<AuthState>(AuthState.Initializing)
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
                        is SessionStatus.Initializing ->
                            AuthState.Initializing
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
                        AuthState.Authenticated -> userRepository.refresh()
                        AuthState.NotAuthenticated -> userRepository.clear()
                        AuthState.Initializing -> Unit
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
        if (state.value == AuthState.Authenticated) {
            // TODO: Call Edge function to delete user
//            val user = userRepository.current()
//            user?.let {
//                client.auth.admin.deleteUser(it.id)
//            }
            client.auth.signOut()
        }
    }

    override fun current(): AuthState = state.value
}