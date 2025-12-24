package com.astrobytes.thedevoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.authentication.AuthProvider
import com.astrobytes.thedevoapp.authentication.AuthState
import com.astrobytes.thedevoapp.models.User
import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.ui.theme.TheDevoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheDevoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Information(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun Information(viewModel: MainViewModel) {
    val auth by viewModel.authState.collectAsState()
    val user by viewModel.userState.collectAsState()

    Column(modifier = Modifier.padding(8.dp)) {
        Text("Auth State: ${auth}")
        Text("User: ${user?.id ?: "No user"}")
        Text("SUPABASE URL: ${BuildConfig.SUPABASE_URL}")
        Text("SUPABASE KEY:${BuildConfig.SUPABASE_KEY}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Button({ viewModel.login() }) {
                Text("Login")
            }
            Button({ viewModel.logout() }) {
                Text("Logout")
            }
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authProvider: AuthProvider,
    private val userRepository: UserRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authProvider.value.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        authProvider.current()
    )

    val userState: StateFlow<User?> = userRepository.value.distinctUntilChanged().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        userRepository.current()
    )

    var errorMessage: String? by mutableStateOf(null)

    fun login() {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                authProvider.login()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown Error"
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                authProvider.logout()
            } catch(e: Exception) {
                errorMessage = e.message ?: "Unknown Error"
            }
        }
    }

    fun clearErrorMessage() {
        errorMessage = null
    }
}
