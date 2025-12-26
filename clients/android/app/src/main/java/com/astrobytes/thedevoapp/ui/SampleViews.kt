package com.astrobytes.thedevoapp.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.BuildConfig
import com.astrobytes.thedevoapp.authentication.AuthProvider
import com.astrobytes.thedevoapp.authentication.AuthState
import com.astrobytes.thedevoapp.models.User
import com.astrobytes.thedevoapp.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    model: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(context) {
        model.openLiveDevotional.collect {
            context.startActivity(Intent(context, LiveDevotionalActivity::class.java))
        }
    }

    Box(modifier = modifier) {
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Information()
            Button(model::onOpenLiveDevotional) {
                Text("Open Live Devotional")
            }
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _openLiveDevotional = MutableSharedFlow<Unit>()
    val openLiveDevotional: SharedFlow<Unit> = _openLiveDevotional
    fun onOpenLiveDevotional() {
        viewModelScope.launch {
            _openLiveDevotional.emit(Unit)
        }
    }
}


@Composable
fun Information(
    modifier: Modifier = Modifier,
    model: InformationViewModel = hiltViewModel()
) {
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
            Button(model::logout) {
                Text("Logout")
            }
        }

        model.errorMessage?.let {
            Text(text = it, color = Color.Red)
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
