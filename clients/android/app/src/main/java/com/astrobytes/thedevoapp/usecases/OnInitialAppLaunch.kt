package com.astrobytes.thedevoapp.usecases

import android.content.Context
import javax.inject.Inject
import androidx.core.content.edit

class OnInitialAppLaunch @Inject constructor(
    context: Context
) {
    private val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    companion object {
        private const val KEY_INITIAL_LAUNCH = "initial_launch"
    }

    private fun isInitialLaunch(): Boolean {
        val isFirstLaunch = preferences.getBoolean(KEY_INITIAL_LAUNCH, true)
        if (isFirstLaunch) {
            preferences.edit { putBoolean(KEY_INITIAL_LAUNCH, false) }
        }
        return isFirstLaunch
    }

    fun execute() {
        if (isInitialLaunch()) {
            // TODO: Perform initial app launch actions
            return
        }
    }
}