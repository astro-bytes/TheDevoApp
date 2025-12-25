package com.astrobytes.thedevoapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astrobytes.thedevoapp.usecases.OnAppLaunch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable fun AppRoot(
    modifier: Modifier,
    model: AppRootViewModel = hiltViewModel()
) {
    val state by model.state.collectAsState()

    when (state) {
        is AppRootViewModel.AppState.Loading -> LoadingView(modifier)
        is AppRootViewModel.AppState.Ready -> MainView(modifier)
        is AppRootViewModel.AppState.Error -> Text((state as AppRootViewModel.AppState.Error).message, modifier = modifier)
    }
}

@HiltViewModel
class AppRootViewModel @Inject constructor(
    private val onAppLaunch: OnAppLaunch
): ViewModel() {
    sealed interface AppState {
        object Loading : AppState
        object Ready : AppState
        data class Error(val message: String) : AppState
    }

    private val _state = MutableStateFlow<AppState>(AppState.Loading)
    val state: StateFlow<AppState> = _state

    init {
        viewModelScope.launch {
            runCatching {
                onAppLaunch.execute()
            }.onSuccess {
                _state.update { AppState.Ready }
            }.onFailure { exception ->
                _state.update {
                    AppState.Error(exception.message ?: "Unknown Error")
                }
            }
        }
    }
}