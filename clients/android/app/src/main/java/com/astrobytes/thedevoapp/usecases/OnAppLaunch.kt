package com.astrobytes.thedevoapp.usecases

import com.astrobytes.thedevoapp.authentication.AuthProvider
import com.astrobytes.thedevoapp.authentication.AuthState
import com.astrobytes.thedevoapp.repositories.UserRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OnAppLaunch @Inject constructor(
    private val authProvider: AuthProvider,
    private val userRepository: UserRepository,
    private val onInitialAppLaunch: OnInitialAppLaunch
) {
    suspend fun execute() {
        onInitialAppLaunch.execute()

        // Wait until authProvider.value emits Authenticated
        authProvider.value
            .filter { it != AuthState.Initializing } // skip initializing
            .first { authState ->
                when (authState) {
                    AuthState.Authenticated -> true
                    AuthState.NotAuthenticated -> {
                        authProvider.login() // trigger login if needed
                        false
                    }
                    AuthState.Initializing -> false
                }
            }

        // At this point, user is authenticated
        userRepository.refresh()
    }
}