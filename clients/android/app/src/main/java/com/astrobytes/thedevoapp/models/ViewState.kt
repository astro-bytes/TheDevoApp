package com.astrobytes.thedevoapp.models

sealed interface ViewState<out T> {
    object Loading : ViewState<Nothing>
    data class Ready<T>(val data: T) : ViewState<T>
    data class Error(val message: String) : ViewState<Nothing>
}
