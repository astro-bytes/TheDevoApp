package com.astrobytes.thedevoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.authentication.AuthProvider
import com.astrobytes.thedevoapp.authentication.AuthState
import com.astrobytes.thedevoapp.models.User
import com.astrobytes.thedevoapp.repositories.UserRepository
import com.astrobytes.thedevoapp.ui.theme.TheDevoAppTheme
import com.astrobytes.thedevoapp.usecases.RecordTap
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheDevoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Information()
        RecordTapView()
    }
}

@Composable
fun Information(
    modifier: Modifier = Modifier
) {
    val model: InformationViewModel = hiltViewModel()

    val auth by model.authState.collectAsState()
    val user by model.userState.collectAsState()

    Column(modifier = modifier.padding(8.dp)) {
        Text("Auth State: $auth")
        Text("User: ${user?.id ?: "No user"}")
        Text("SUPABASE URL: ${BuildConfig.SUPABASE_URL}")
        Text("SUPABASE KEY:${BuildConfig.SUPABASE_KEY}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Button(model::login) {
                Text("Login")
            }
            Button(model::logout) {
                Text("Logout")
            }
        }

        model.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }
    }
}

@Composable
fun RecordTapView(
    modifier: Modifier = Modifier
) {
    val model: RecordTapModel = hiltViewModel()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Button(model::onButtonTapped) {
                Text("Record a Tap")
            }

            model.errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val authProvider: AuthProvider,
    userRepository: UserRepository
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

@HiltViewModel
class RecordTapModel @Inject constructor(
    private val recordTap: RecordTap
): ViewModel() {
    var errorMessage: String? by mutableStateOf(null)

    fun onButtonTapped() {
        viewModelScope.launch {
            try {
                clearErrorMessage()
                recordTap.execute(1)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error occurred"
            }
        }
    }

    fun clearErrorMessage() {
        errorMessage = null
    }
}
